# turtleTimeKt
Interpreter for Turtle Time esolang 

To compile, run `./gradlew build` 

compiled file is in `build/libs`

launch by running `java -jar turtleTime.jar [parameters] [code file]`

use `-h` to see list of options for current interpreter

when input is required in normal mode, `>` appears. Entered symbols will be used within the program, including line break (whether successfully or not depends solely on turtles)

also, program crashes when you input non-rectangular code grid. This is normal, as turtleTime grids can only be rectangular.
