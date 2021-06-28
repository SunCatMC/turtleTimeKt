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
    private val canFallAsleep: Boolean
        get() = currentCharBelow !in CodeGroup.water_like
    val currentCharBelow: Char
        get() = charBelow(x, y)
    private fun charBelow(x: Int, y: Int) = world.grid[x, y]

    operator fun invoke() {
        if (currentCharBelow in CodeGroup.walls)
            throw Exception("turtle in a wall wut (at x $x y $y)")
        move(Direction.straightList.random())
    }

    private fun move(direction: Direction) {
        if (isAsleep) return //TODO: don't forget about conveyors!
        val xy = Coordinates(this.x + direction.x, this.y + direction.y)
        val (x,y) = xy
        val ch = charBelow(x, y)
        val isThereTurtle = world.turtleStorage[x, y] != null
        if (ch !in CodeGroup.walls && !isThereTurtle) {
            val (oldX, oldY) = this.xy
            this.xy = xy
            world.turtleStorage.update(oldX, oldY,this)
            return
        }
        if (canFallAsleep && (ch == CodeUnit.WALL || isThereTurtle || currentCharBelow in CodeGroup.ice_like)) {
            isAsleep = true
        }
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