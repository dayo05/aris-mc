## aris.log_debug(msg: string)
## aris.log_info(msg: string)
## aris.log_warn(msg: string)
## aris.log_error(msg: string)
## aris.check_version(v: string)
## aris.math.create_rect_area(x: number, y: number, width: number, height: number) -> Area
```
 @param x x of left-top point
 @param y y of left-top point
 @param width width of the area
 @param height height of the area
 @return Area(x with y, x with (y + height), (x + width) with (y + height), (x + width) with y)
```
## aris.math.create_rect_area(p1: Point, p2: Point) -> Area
```
 @return createRect(min(p1.x, p2.x), min(p1.y, p2.y), abs(p1.x - p2.x), abs(p1.y - p2.y))
```
## aris.math.create_point(x: number, y: number) -> Point
```
 Create point object (x, y)
 @return Point(x, y)
```
## aris.math.create_point(x: number, y: number, z: number) -> Point3
```
 Create point object (x, y, z)
 @return Point3(x, y, z)
```


## AreaBuilder:append(p: Point)
```
 Append the point that constructs node of the area
```


## AreaBuilder:build() -> Area


## Area:is_in(p: Point) -> boolean


## Area:times(x: number) -> Area


## Area:center() -> Point


## Area:into_string() -> string


## Point3:minus(other: Point3) -> Point


## Point3:plus(other: Point3) -> Point


## Point3:div(other: number) -> Point


## Point3:center(other: Point3) -> Point


## Point3:into_string() -> string


## Point3:set_x(new_value: number)


## Point3:get_x() -> number


## Point3:set_y(new_value: number)


## Point3:get_y() -> number


## Point3:set_z(new_value: number)


## Point3:get_z() -> number


## Point:minus(other: Point) -> Point


## Point:plus(other: Point) -> Point


## Point:div(other: number) -> Point


## Point:center(other: Point) -> Point


## Point:into_string() -> string


## Point:set_x(new_value: number)


## Point:get_x() -> number


## Point:set_y(new_value: number)


## Point:get_y() -> number
