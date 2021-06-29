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
        get() = world.charBelow(xy)

    operator fun invoke() {
        when (currentCharBelow) {
            in CodeGroup.walls -> throw Exception("turtle in a wall wut (at x == $x, y == $y)")
            in CodeGroup.duplicators -> {
                dupe()
                return
            }
            CodeUnit.CONDITION -> if (value != 0) move(currentDirection)
        }
        if (move(currentDirection)) {
            updateValue()
        }
        if (isAsleep) return
        if (!isSliding) {
            currentDirection = randomDirection()
        }
    }

    private fun move(direction: Direction, isCausedByConveyor:Boolean = false): Boolean {
        if (!isAsleep || isCausedByConveyor) {
            val xy = getMovementCoordinates(direction)
            val isBlocked = world.isPositionBlocked(xy, this)
            if (!isBlocked) {
                updatePosition(xy)
                return true
            }
            if (canFallAsleep && (world.charBelow(xy) != CodeUnit.SOFT_WALL || isSliding)) {
                isAsleep = true
            }
        }
        if (!isCausedByConveyor) {
            return getPushedByConveyorIfNeeded()
        }
        return false
    }

    private fun dupe() {
        val positions = CodeGroup.mapDuplicatorToDirections.getValue(currentCharBelow).map {
            getMovementCoordinates(it)
        }.toSet().filter {
            !world.isPositionBlocked(it, this)
        }.shuffled()
        if (positions.isNotEmpty()) {
            if (world.isPositionBlocked(positions.first(), this)) throw Exception("wait, position at ${positions.first()} is obstructed??")
            val copyTurtle = this.copy()
            updatePosition(positions.first())
            updateValue()
            if (positions.size > 1) {
                if (world.isPositionBlocked(positions.last(), copyTurtle)) throw Exception("wait, position at ${positions.last()} is obstructed??")
                world.turtleStorage.add(copyTurtle)
                copyTurtle.updatePosition(positions.last())
                copyTurtle.updateValue()
            }
        } else {
            isAsleep = true
        }
    }

    private fun copy(): Turtle {
        return Turtle(x, y, world).let {
            it.value = this.value
            it.currentDirection = this.currentDirection
            it.isAsleep = this.isAsleep
            it
        }
    }
    
    private fun updateValue() {
        if (currentCharBelow in CodeGroup.constants) {
            this.value = currentCharBelow.digitToInt(16)
        }
        if (isAsleep) return
    }

    private fun updatePosition(xy: Coordinates) {
        val (oldX, oldY) = this.xy
        this.xy = xy
        world.turtleStorage.update(oldX, oldY,this)
    }

    private fun getMovementCoordinates(direction: Direction): Coordinates {
        return world.grid.mapCoordinatesToGrid(Coordinates(this.x + direction.x, this.y + direction.y))
    }

    private fun getPushedByConveyorIfNeeded(): Boolean {
        if (currentCharBelow !in CodeGroup.conveyors) return false
        return this.move(CodeGroup.mapConveyorToDirection.getValue(currentCharBelow), true)
    }

    override fun toString(): String {
        return "Turtle(xy=$xy, value=$value, isAsleep=$isAsleep, currentDirection=$currentDirection," +
                "canFallAsleep=$canFallAsleep, isSliding=$isSliding, currentCharBelow=\'$currentCharBelow\')"
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