package me.ddayo.aris.engine.wrapper

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import me.ddayo.aris.engine.InGameEngine
import me.ddayo.aris.engine.InitEngine
import me.ddayo.aris.engine.MCBaseEngine
import me.ddayo.aris.lua.glue.InGameGenerated
import me.ddayo.aris.luagen.ILuaStaticDecl
import me.ddayo.aris.luagen.LuaFunction
import me.ddayo.aris.luagen.LuaProvider
import me.ddayo.aris.luagen.RetrieveEngine
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.IntArrayTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongArrayTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.ShortTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.TagParser
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import org.apache.logging.log4j.LogManager
import party.iroiro.luajava.Lua
import party.iroiro.luajava.value.LuaValue

@LuaProvider(InGameEngine.PROVIDER)
@LuaProvider(InitEngine.PROVIDER)
class LuaNBTCompound(val inner: CompoundTag): ILuaStaticDecl by InGameGenerated.LuaNBTCompound_LuaGenerated {
    /**
     * Convert NBT into JSON string
     */
    @LuaFunction("into_string")
    fun intoJsonString() = inner.toString()

    /**
     * Convert NBT into Lua Table
     */
    @LuaFunction("into_table")
    fun intoTable(@RetrieveEngine engine: MCBaseEngine): LuaValue {
        return NbtToLuaConverter.convert(engine, inner)
    }

    /**
     * Convert NBT into item stack
     */
    @LuaFunction("into_item_stack")
    fun intoItemStack(): LuaItemStack {
        return LuaItemStack(ItemStack.of(inner))
    }

    /**
     * Apply(overwrite) current NBT into entity
     */
    @LuaFunction("apply_entity")
    fun applyEntity(entity: LuaEntity) {
        val uid = if(inner.contains("UUID"))
            inner.getUUID("UUID").also {
                inner.remove("UUID")
            } else null

        entity.inner.load(inner)
        refreshEntityState(entity.inner)
        uid?.let {
            inner.putUUID("UUID", uid)
        }
    }

    /**
     * Spawn entity with this NBT
     */
    @LuaFunction("spawn_entity")
    fun spawnEntity(level: LuaServerWorld): LuaEntity? {
        val uid = if(inner.contains("UUID"))
            inner.getUUID("UUID").also {
                inner.remove("UUID")
            } else null

        val level = level.inner
        val entityOpt = EntityType.create(inner, level)

        val entity = if (entityOpt.isPresent) {
            val entity = entityOpt.get()
            level.addFreshEntity(entity)
            LuaEntity(entity)
        } else {
            null
        }

        uid?.let {
            inner.putUUID("UUID", uid)
        }
        return entity
    }

    /**
     * Places block entity with this NBT
     * If existing then it replaces
     * @return is successful
     */
    @LuaFunction("place_block_entity")
    fun placeBlockEntity(level: LuaServerWorld): Boolean {
        if (!hasPositionData()) return false
        val level = level.inner

        val pos = getPosition()

        val requiredIdStr = inner.getString("id")
        val requiredLocation = ResourceLocation.tryParse(requiredIdStr)

        if (requiredLocation != null) {
            val currentBlockState = level.getBlockState(pos)
            val currentBlockId = BuiltInRegistries.BLOCK.getKey(currentBlockState.block)

            if (currentBlockId != requiredLocation) {
                val newBlock = BuiltInRegistries.BLOCK.get(requiredLocation)
                level.setBlock(pos, newBlock.defaultBlockState(), 3)
            }
        }

        val state = level.getBlockState(pos)
        val blockEntity = BlockEntity.loadStatic(pos, state, inner)

        if (blockEntity != null) {
            level.setBlockEntity(blockEntity)
            blockEntity.setChanged()
            level.sendBlockUpdated(pos, state, state, 3)
            return true
        }
        return false
    }

    /**
     * Place block with this NBT at provided position
     * If exists then it replaces
     * @return is successful
     */
    @LuaFunction("place_block_state")
    fun placeBlockState(level: LuaServerWorld, x: Int, y: Int, z: Int): Boolean {
        val state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), inner)
        return level.inner.setBlock(BlockPos(x, y, z), state, 3)
    }

    private fun getPosition() = BlockPos(inner.getInt("x"), inner.getInt("y"), inner.getInt("z"))

    private fun hasPositionData() = inner.contains("x") && inner.contains("y") && inner.contains("z")

    private fun refreshEntityState(e: net.minecraft.world.entity.Entity) {
        val level = e.level()
        if (level !is ServerLevel) return

        if (e is ServerPlayer) {
            e.teleportTo(level, e.x, e.y, e.z, e.yRot, e.xRot)
            e.inventoryMenu.broadcastChanges() // Sync Inventory UI
        } else e.absMoveTo(e.x, e.y, e.z, e.yRot, e.xRot)

        e.refreshDimensions()

        val packedData = e.entityData.packDirty()
        if (packedData != null) {
            val packet = ClientboundSetEntityDataPacket(e.id, packedData)
            level.chunkSource.broadcast(e, packet)
        }
    }

    private object NbtToLuaConverter {
        fun convert(engine: MCBaseEngine, tag: Tag): LuaValue {
            val lua = engine.lua
            return when (tag) {
                is CompoundTag -> {
                    lua.createTable(0, tag.size())
                    val table = lua.get()
                    for (key in tag.allKeys) {
                        tag.get(key)?.let { table.set(key, convert(engine, it)) }
                    }
                    table
                }
                is ListTag -> {
                    lua.createTable(tag.size, 1)
                    val table = lua.get()
                    for (i in 0 until tag.size) {
                        table.set(i + 1, convert(engine, tag[i]))
                    }
                    table.set("__aris_nbt_type", lua.from(getTypeName(tag.elementType)))
                    table
                }
                is StringTag -> lua.from(tag.asString)
                is IntTag -> lua.from(tag.asInt.toLong())
                is DoubleTag -> lua.from(tag.asDouble)
                is FloatTag -> lua.from(tag.asFloat.toDouble())
                is LongTag -> lua.from(tag.asLong)
                is ShortTag -> lua.from(tag.asShort.toLong())
                is ByteTag -> lua.from(tag.asByte.toLong())
                is ByteArrayTag -> {
                    lua.createTable(tag.size, 1)
                    val table = lua.get()
                    tag.asByteArray.forEachIndexed { i, b -> table.set(i + 1, lua.from(b.toLong())) }
                    table.set("__aris_nbt_type", lua.from("byte_array"))
                    table
                }
                is IntArrayTag -> {
                    lua.createTable(tag.size, 1)
                    val table = lua.get()
                    tag.asIntArray.forEachIndexed { i, v -> table.set(i + 1, lua.from(v.toLong())) }
                    table.set("__aris_nbt_type", lua.from("int_array"))
                    table
                }
                is LongArrayTag -> {
                    lua.createTable(tag.size, 1)
                    val table = lua.get()
                    tag.asLongArray.forEachIndexed { i, v -> table.set(i + 1, lua.from(v)) }
                    table.set("__aris_nbt_type", lua.from("long_array"))
                    table
                }
                else -> {
                    lua.pushNil()
                    lua.get()
                }
            }
        }

        private fun getTypeName(id: Byte): String {
            return when (id.toInt()) {
                Tag.TAG_BYTE.toInt() -> "byte"
                Tag.TAG_SHORT.toInt() -> "short"
                Tag.TAG_INT.toInt() -> "int"
                Tag.TAG_LONG.toInt() -> "long"
                Tag.TAG_FLOAT.toInt() -> "float"
                Tag.TAG_DOUBLE.toInt() -> "double"
                Tag.TAG_BYTE_ARRAY.toInt() -> "byte_array"
                Tag.TAG_STRING.toInt() -> "string"
                Tag.TAG_LIST.toInt() -> "list"
                Tag.TAG_COMPOUND.toInt() -> "compound"
                Tag.TAG_INT_ARRAY.toInt() -> "int_array"
                Tag.TAG_LONG_ARRAY.toInt() -> "long_array"
                else -> "unknown"
            }
        }
    }
}

@LuaProvider(InGameEngine.PROVIDER, library = "aris.game.nbt")
@LuaProvider(InitEngine.PROVIDER, library = "aris.init.nbt")
object LuaNBTCompoundFunctions {
    /**
     * Converts Lua Table into NBT Compound
     */
    @LuaFunction("from_table")
    fun fromTable(table: LuaValue): LuaNBTCompound {
        if (table.type() != Lua.LuaType.TABLE) return LuaNBTCompound(CompoundTag())
        val tag = convert(table)
        return if (tag is CompoundTag) LuaNBTCompound(tag) else LuaNBTCompound(CompoundTag())
    }

    /**
     * Get NBT of entity
     * @param entity entity to get nbt
     * @return full nbt object of entity
     */
    @LuaFunction("from_entity")
    fun fromEntity(entity: LuaEntity) = LuaNBTCompound(CompoundTag().also {
        if (!entity.inner.save(it))
            entity.inner.saveWithoutId(it)
    })

    /**
     * Get NBT of item stack
     * @param stack item stack to get nbt
     * @return full nbt object of item stack
     */
    @LuaFunction("from_item_stack")
    fun fromItemStack(stack: LuaItemStack) = LuaNBTCompound(CompoundTag().also {
        stack.inner.save(it)
    })

    /**
     * Get NBT of block entity at specific position
     * @param level server level for target block entity
     * @param x x position for target block entity
     * @param y y position for target block entity
     * @param z z position for target block entity
     * @return full nbt object of provided location. Nil if not exists.
     */
    @LuaFunction("from_block_entity")
    fun fromBlockEntity(level: LuaServerWorld, x: Int, y: Int, z: Int) = level.inner.getBlockEntity(BlockPos(x, y, z))?.saveWithId()?.let { LuaNBTCompound(it) }

    /**
     * Get NBT of block state at specific position
     * @param level server level for target block state
     * @param x x position for target block state
     * @param y y position for target block state
     * @param z z position for target block state
     * @return full nbt object of provided location
     */
    @LuaFunction("from_block_state")
    fun fromBlockState(level: LuaServerWorld, x: Int, y: Int, z: Int) =
        LuaNBTCompound(NbtUtils.writeBlockState(level.inner.getBlockState(BlockPos(x, y, z))))

    /**
     * Converts string into NBT
     * @param string nbt string to convert into nbt
     * @return nbt object of provided string
     */
    @LuaFunction("from_string")
    fun fromString(string: String) = LuaNBTCompound(
        TagParser.parseTag(string)
    )

    fun convert(value: LuaValue): Tag {
        if (value.type() == Lua.LuaType.TABLE) {
            val explicitType = value.get("__aris_nbt_type")

            if (explicitType.type() != Lua.LuaType.NIL) {
                val typeStr = explicitType.toString()
                when (typeStr) {
                    "byte_array" -> return ByteArrayTag(readByteList(value))
                    "int_array" -> return IntArrayTag(readIntList(value))
                    "long_array" -> return LongArrayTag(readLongList(value))
                    "byte" -> return createTypedList(value) { ByteTag.valueOf(it.toInteger().toByte()) }
                    "short" -> return createTypedList(value) { ShortTag.valueOf(it.toInteger().toShort()) }
                    "int" -> return createTypedList(value) { IntTag.valueOf(it.toInteger().toInt()) }
                    "long" -> return createTypedList(value) { LongTag.valueOf(it.toInteger()) }
                    "float" -> return createTypedList(value) { FloatTag.valueOf(it.toNumber().toFloat()) }
                    "double" -> return createTypedList(value) { DoubleTag.valueOf(it.toNumber()) }
                    "string" -> return createTypedList(value) { StringTag.valueOf(it.toString()) }
                    "list" -> return createTypedList(value) { convert(it) } // Generic list of Lists
                    "compound" -> {
                        val compound = CompoundTag()
                        iterateTable(value) { k, v ->
                            if (k.toString() != "__aris_nbt_type") {
                                compound.put(k.toString(), convert(v))
                            }
                        }
                        return compound
                    }
                }
            }

            LogManager.getLogger().warn("No type information (__aris_nbt_type) provided for NBT conversion. Table content: ${deepToString(value)}")

            // Heuristic Fallback
            if (isArray(value)) {
                val list = ListTag()
                var i = 1
                while (true) {
                    val v = value.get((i++))
                    if (v.type() == Lua.LuaType.NIL) break
                    list.add(convert(v))
                }
                return list
            } else {
                val compound = CompoundTag()
                iterateTable(value) { k, v ->
                    if (k.type() == Lua.LuaType.STRING && k.toString() != "__aris_nbt_type") {
                        compound.put(k.toString(), convert(v))
                    }
                }
                return compound
            }
        } else if (value.type() == Lua.LuaType.STRING) {
            return StringTag.valueOf(value.toString())
        } else if (value.type() == Lua.LuaType.BOOLEAN) {
            return ByteTag.valueOf(if (value.toBoolean()) 1 else 0)
        } else if (value.type() == Lua.LuaType.NUMBER) {
            // Check if integer or float
            val num = value.toNumber()
            if (num % 1.0 == 0.0) {
                return IntTag.valueOf(num.toInt())
            }
            return DoubleTag.valueOf(num)
        }
        return StringTag.valueOf(value.toString())
    }

    private fun createTypedList(table: LuaValue, mapper: (LuaValue) -> Tag): ListTag {
        val list = ListTag()
        var i = 1
        while (true) {
            val v = table.get(i++)
            if (v.type() == Lua.LuaType.NIL) break
            list.add(mapper(v))
        }
        return list
    }

    private fun readByteList(table: LuaValue): MutableList<Byte> {
        val list = ArrayList<Byte>()
        var i = 1
        while (true) {
            val v = table.get(i++)
            if (v.type() == Lua.LuaType.NIL) break
            list.add(v.toInteger().toByte())
        }
        return list
    }

    private fun readIntList(table: LuaValue): MutableList<Int> {
        val list = ArrayList<Int>()
        var i = 1
        while (true) {
            val v = table.get(i++)
            if (v.type() == Lua.LuaType.NIL) break
            list.add(v.toInteger().toInt())
        }
        return list
    }

    private fun readLongList(table: LuaValue): MutableList<Long> {
        val list = ArrayList<Long>()
        var i = 1
        while (true) {
            val v = table.get(i++)
            if (v.type() == Lua.LuaType.NIL) break
            list.add(v.toInteger())
        }
        return list
    }

    private inline fun iterateTable(table: LuaValue, action: (LuaValue, LuaValue) -> Unit) = table.forEach {(key, value) -> action(key, value)}

    private fun isArray(table: LuaValue): Boolean {
        return table.length() > 0
    }

    private fun deepToString(v: LuaValue, depth: Int = 0): String {
        if (depth >= 5) return "{...}"
        if (v.type() == Lua.LuaType.TABLE) {
            val sb = StringBuilder("{")
            var count = 0
            v.forEach { (key, value) ->
                if (count++ > 8) {
                    sb.append(", ...")
                    return@forEach
                }
                if (count > 1) sb.append(", ")
                sb.append(key.toString()).append("=").append(deepToString(value as LuaValue, depth + 1))
            }
            sb.append("}")
            return sb.toString()
        }
        return v.toString()
    }
}
