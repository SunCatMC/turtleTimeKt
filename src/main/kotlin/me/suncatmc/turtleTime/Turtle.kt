package me.suncatmc.turtleTime

class Turtle(x: Int, y: Int, val world: World) {
    var x = x
        private set(value) {
            field = value.mod(world.grid.rowSize)
        }
    var y = y
        private set(value) {
            field = value.mod(world.grid.columnSize)
        }
    var value = 0
        private set
    var isAsleep = false
    private val canFallAsleep: Boolean
        get() = currentCharBelow !in CodeGroup.water_like
    private val currentCharBelow: Char
        get() = charBelow(x, y)
    private fun charBelow(x: Int, y: Int) = world.grid[x, y]

    operator fun invoke() {
        if (currentCharBelow in CodeGroup.walls)
            throw Exception("turtle in a wall wut (at x $x y $y)")
        move(Direction.straightList.random())
    }

    private fun move(direction: Direction) {
        if (isAsleep) return //TODO: don't forget about conveyors!
        val x = this.x + direction.x
        val y = this.y + direction.y
        val ch = charBelow(x, y)
        if (ch !in CodeGroup.walls) {
            this.x = x
            this.y = y
            return
        }
        if (canFallAsleep && (ch == CodeUnit.WALL || currentCharBelow in CodeGroup.ice_like)) {
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