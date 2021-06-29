package me.suncatmc.turtleTime

class Turtle(x: Int, y: Int, private val world: World) {
    var x = x
        private set(value) {
            field = value.mod(world.grid.rowSize)
        }
    var y = y
        private set(value) {
            field = value.mod(world.grid.columnSize)
        }
    var xy: Coordinates
        get() = Coordinates(x, y)
        set(value) {
            x = value.x
            y = value.y
        }
    var value = 0
        private set
    var isAsleep = false

    private var currentDirection = randomDirection()
    private fun randomDirection() = Direction.straightList.random()

    private val canFallAsleep: Boolean
        get() = !isAsleep && currentCharBelow !in CodeGroup.water_like
    private val isSliding: Boolean
        get() = currentCharBelow in CodeGroup.ice_like
    val currentCharBelow: Char
        get() = charBelow(x, y)
    private fun charBelow(x: Int, y: Int) = world.grid[x, y]

    operator fun invoke() {
        if (currentCharBelow in CodeGroup.walls) {
            throw Exception("turtle in a wall wut (at x == $x, y == $y)")
        }
        if (currentCharBelow == CodeUnit.CONDITION && value != 0) {
            move(currentDirection)
        }
        move(currentDirection)
        if (currentCharBelow in CodeGroup.constants)
            this.value = currentCharBelow.digitToInt(16)
        if (isAsleep) return
        if (!isSliding) {
            currentDirection = randomDirection()
        }
    }

    private fun move(direction: Direction, isCausedByConveyor:Boolean = false) {
        if (!isAsleep || isCausedByConveyor) {
            val xy = getMovementCoordinates(direction)
            val (x,y) = xy
            val ch = charBelow(x, y)
            val isThereTurtle = world.turtleStorage[x, y, TurtleTime.FUTURE].let {it != null && it !== this}
            if (ch !in CodeGroup.walls && !isThereTurtle) {
                val (oldX, oldY) = this.xy
                updatePosition(xy)
                world.turtleStorage.update(oldX, oldY,this)
                return
            }
            if (canFallAsleep && (ch == CodeUnit.WALL || isThereTurtle || isSliding)) {
                isAsleep = true
            }
        }
        if (!isCausedByConveyor) {
            getPushedByConveyorIfNeeded()
        }
    }

    private fun updatePosition(xy: Coordinates) {
        this.xy = xy
    }

    private fun getMovementCoordinates(direction: Direction): Coordinates {
        return Coordinates(this.x + direction.x, this.y + direction.y)
    }

    private fun getPushedByConveyorIfNeeded() {
        if (currentCharBelow !in CodeGroup.conveyors) return
        this.move(CodeGroup.mapConveyorToDirection.getValue(currentCharBelow), true)
    }

    override fun toString(): String {
        return "Turtle(xy=$xy, value=$value, isAsleep=$isAsleep, currentDirection=$currentDirection, canFallAsleep=$canFallAsleep, isSliding=$isSliding, currentCharBelow=$currentCharBelow)"
    }

}

//no one will know this was taken from stackoverflow
private fun operatorFromChar(charOperator: Char):(Int, Int)->Int
{
    return when(charOperator)
    {
        '+'->{a,b->a+b}
        '-'->{a,b->a-b}
        '/'->{a,b->a/b}
        '*'->{a,b->a*b}
        '%'->{a,b->a%b}
        else -> throw Exception("That's not a supported operator")
    }
}