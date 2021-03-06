package me.suncatmc.turtleTime

object CodeGroup {
    val inputs = setOf(CodeUnit.INPUT_CH, CodeUnit.INPUT_NUM)
    val houses = setOf(CodeUnit.HOUSE_CH, CodeUnit.HOUSE_NUM)
    val duplicators = setOf(CodeUnit.DUPE_HOR, CodeUnit.DUPE_VER)
    val math = setOf(CodeUnit.PLUS, CodeUnit.MINUS, CodeUnit.DIV, CodeUnit.MOD, CodeUnit.MUL)
    val portals = setOf(CodeUnit.PORTAL_A, CodeUnit.PORTAL_B, CodeUnit.PORTAL_C, CodeUnit.PORTAL_D,
        CodeUnit.PORTAL_E, CodeUnit.PORTAL_F)
    val conveyors = setOf(CodeUnit.CONVEYOR_DOWN, CodeUnit.CONVEYOR_LEFT, CodeUnit.CONVEYOR_RIGHT, CodeUnit.CONVEYOR_UP)
    val walls = setOf(CodeUnit.WALL, CodeUnit.SOFT_WALL)
    val water_like = setOf(CodeUnit.WATER) + math
    val ice_like = setOf(CodeUnit.ICE, CodeUnit.CONDITION) + duplicators
    val turtles = setOf(CodeUnit.TURTLE_MAIN, CodeUnit.TURTLE_WATER, CodeUnit.TURTLE_ICE)
    val constants = ('0'..'9').toSet() + ('A'..'F').toSet()
    val mapTurtleToCell = mapOf(CodeUnit.TURTLE_MAIN to CodeUnit.GRASS, CodeUnit.TURTLE_WATER to CodeUnit.WATER,
        CodeUnit.TURTLE_ICE to CodeUnit.ICE)
    val mapCellToTurtle = mapTurtleToCell.entries.associateBy({ it.value }) { it.key } +
            water_like.associateWith { CodeUnit.TURTLE_WATER } + ice_like.associateWith { CodeUnit.TURTLE_ICE }
    val mapConveyorToDirection = mapOf(CodeUnit.CONVEYOR_DOWN to Direction.DOWN,
        CodeUnit.CONVEYOR_LEFT to Direction.LEFT, CodeUnit.CONVEYOR_RIGHT to Direction.RIGHT,
        CodeUnit.CONVEYOR_UP to Direction.UP)
    val mapDuplicatorToDirections = mapOf(CodeUnit.DUPE_HOR to setOf(Direction.LEFT, Direction.RIGHT),
        CodeUnit.DUPE_VER to setOf(Direction.UP, Direction.DOWN), )
}