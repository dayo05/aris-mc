# Project Aris

Project Aris is a Minecraft 1.21.4 scripting mod. It embeds LuaJIT into the
game and exposes Minecraft-side APIs to Lua scripts so gameplay behavior,
items, particles, hooks, client UI, key bindings, and client/server packets can
be extended without recompiling the mod.

The mod is built with Architectury, Kotlin, and Gradle. Shared behavior lives in
`common`, while `fabric` and `neoforge` provide the loader-specific entrypoints,
network registration, registry integration, and packaging.

## What This Mod Does

Aris loads Lua scripts from a `robots` directory and runs them in different Lua
engines depending on lifecycle and logical side.

- Server/client init scripts can register Minecraft content such as scriptable
  items and particles during startup.
- Server in-game scripts can react to player joins, leaves, ticks, block
  interactions, entity events, item events, chat, commands, and custom packets.
- Client scripts can build UI/HUD behavior, register sounds, handle custom key
  bindings, store synced values from the server, and run client-side game hooks.
- Scripts can use generated Lua bindings for Minecraft wrappers such as
  entities, players, worlds, item stacks, NBT compounds, damage sources, and
  events.
- `/aris reload` reloads the runtime Lua engines and asks connected clients to
  reload their client-side engines too.

In short, the compiled mod provides the platform bridge and API surface; the
`robots` scripts provide the actual dynamic gameplay logic.

## Script Layout

Aris searches these directories at runtime:

- `robots/init` - startup scripts for shared server/client initialization.
- `robots/client-init` - client-only startup scripts, such as client particles
  and sounds.
- `robots/game` - server in-game scripts loaded when the server starts.
- `robots/client` - client scripts loaded after the Minecraft client starts.
- `robots/client-game` - client in-world scripts loaded when joining a world.

Each file in a script directory is scheduled as a Lua task. The file name
without its extension becomes the task name for the persistent engines.

Scripts can coordinate load order with:

```lua
depends_on("other_task_name")
```

Internally this yields the current Lua task until the dependency task has been
processed.

## Runtime Architecture

The core engine type is `MCBaseEngine`, which extends `LuaEngine` from
`me.ddayo:aris.luagen`. It initializes the generated base Lua library, tracks
processed tasks, supports cooperative task yielding, and owns engine-dispose
callbacks.

Aris uses several specialized engines:

- `InitEngine` loads `robots/init`, exposes `aris.init`, and is disposed once
  startup initialization finishes.
- `ClientInitEngine` extends init behavior with `robots/client-init` and
  client-only APIs.
- `InGameEngine` loads `robots/game`, persists while the server is running, and
  owns server tick hooks.
- `ClientMainEngine` loads `robots/client`, persists for the Minecraft client
  lifetime, and owns client-side UI/runtime APIs.
- `ClientInGameEngine` loads `robots/client-game`, exists only while joined to a
  world, handles client world ticks, key hooks, and server-synced values.

Persistent engines are managed by `AbstractPersistentEngineCompanion`. On
reload or shutdown it:

- fires Lua `on_engine_dispose` callbacks,
- runs native dispose hooks,
- clears registered hooks and hook maps,
- drops the current engine instance,
- creates a fresh LuaJIT-backed engine when needed.

## Lua API Generation

Kotlin APIs are exported to Lua with annotations from `aris.luagen`.

Examples:

- `@LuaProvider(library = "aris")` exposes base functions such as logging and
  file access.
- `@LuaProvider(InitEngine.PROVIDER, library = "aris.init")` exposes init-only
  functions such as item and particle registration.
- `@LuaProvider(InGameEngine.PROVIDER, library = "aris.game")` exposes in-game
  server functions such as command dispatch, selectors, item stack creation,
  effects, and entity spawning.
- `@LuaCallback` marks Lua callback parameters for hooks.
- `@RetrieveEngine` injects the current engine into provider functions.

KSP generates the glue classes under package `me.ddayo.aris.lua.glue`, and the
generated API documentation is committed in `docs/`.

## Hooks And Events

Hooks are represented by `LuaHook` and `LuaHookMap`. Lua scripts register
callbacks through generated functions, while mixins and platform entrypoints call
the hook executors from Minecraft events.

Important hook areas include:

- game tick callbacks,
- player join and leave callbacks,
- block left click, right click, break, and place callbacks,
- entity interaction and attack callbacks,
- item movement, consumption, and use callbacks,
- chat and command callbacks,
- client key press and client tick callbacks,
- custom client/server packet callbacks.

Some event wrappers are cancellable. For example, block and entity interaction
events can call `event:cancel()` from Lua, and the Kotlin hook executor returns
that cancellation state back to the Minecraft mixin.

## Networking

Aris registers custom payloads during common initialization:

- `left_click` - client-to-server signal for client left-click detection.
- `generic_c2s` - Lua-declared client-to-server packets.
- `generic_s2c` - Lua-declared server-to-client packets.
- `sync_data` - server-to-client value sync for strings, numbers, and item
  stacks.
- `reload_engine` - server-to-client signal that reloads client Lua engines.

`NetworkingExtensions` is an Architectury `@ExpectPlatform` bridge. The common
code declares what must happen, while `fabric` and `neoforge` provide the actual
loader-specific packet registration and send implementations.

## Reload Behavior

The `/aris reload` command reloads the server in-game engine. Before disposal,
currently connected players are sent through the old engine's leave hooks so
scripts can clean up state. After the new engine is created, those same players
are sent through join hooks so the new scripts see a fresh startup-like world
state.

The server also sends a `reload_engine` payload to connected clients. Client
reload disposes and recreates `ClientMainEngine`, then recreates
`ClientInGameEngine` if the player is currently in a world.

## Commands

Aris registers `/aris` commands:

- `/aris reload` - reload server and client Lua runtime scripts.
- `/aris set_value <player> <name> item <item>` - sync an item stack value to a
  client.
- `/aris set_value <player> <name> number <number>` - sync a numeric value to a
  client.
- `/aris set_value <player> <name> string <string>` - sync a string value to a
  client.

Additional script-defined commands are registered through the command builder
functions.

## Modules

- `common` - shared mod code, Lua engines, generated bindings, wrappers,
  hooks, commands, networking declarations, and common mixins.
- `fabric` - Fabric loader entrypoints, platform networking, registry helpers,
  mixins, and packaging.
- `neoforge` - NeoForge entrypoints, platform runtime dependencies, networking,
  registry helpers, and packaging.
- `docs` - generated Lua API documentation.

## Requirements

- JDK 21
- Gradle wrapper from this repository (`./gradlew`)

## Build

Build all modules:

```sh
./gradlew build
```

Build a specific platform:

```sh
./gradlew fabric:build
./gradlew neoforge:build
```

Packaged mod jars are written under each platform module's `build/libs`
directory.

## Run For Development

Use the platform module names from this repository:

```sh
./gradlew fabric:runClient
./gradlew neoforge:runClient
```

For server-side testing:

```sh
./gradlew fabric:runServer
./gradlew neoforge:runServer
```

Do not use the old `forge:*` task path; this project uses the `neoforge`
module.

## Lua API Docs

Generated API documentation is committed in `docs/`:

- `docs/LuaGenerated_doc.md`
- `docs/InitGenerated_doc.md`
- `docs/InGameGenerated_doc.md`
- `docs/LuaClientOnlyGenerated_doc.md`
- `docs/ClientInitGenerated_doc.md`
- `docs/ClientInGameOnlyGenerated_doc.md`

## Project Versions

Current versions are configured in `gradle.properties`:

- Minecraft: `1.21.4`
- Mod version: `1.1.0`
- Fabric Loader: `0.18.4`
- Fabric API: `0.119.4+1.21.4`
- NeoForge: `21.4.156`
