package me.suncatmc.turtleTime

class TurtleStorage(val rowSize: Int, val columnSize: Int) {
    var allList = listOf<Turtle>()
        private set
    val movableList: List<Turtle>
        get() = allList.filter {
            !it.isAsleep || it.currentCharBelow in CodeGroup.conveyors || it.currentCharBelow in CodeGroup.duplicators
        }
    private val allListBuffer = mutableListOf<Turtle>()
    private var turtleTree = mapOf<Int, Map<Int, Turtle>>()
    private val turtleTreeBuffer = mutableMapOf<Int, MutableMap<Int, Turtle>>()

    operator fun get(x: Int, y: Int, time: TurtleTime): Turtle? {
        val tree = if (time == TurtleTime.FUTURE) turtleTreeBuffer else turtleTree
        return tree[y.mod(columnSize)]?.get(x.mod(rowSize))
    }

    private fun addToTree(turtle: Turtle) {
        val turtleRow = turtleTreeBuffer[turtle.y] ?: run {
            turtleTreeBuffer[turtle.y] = mutableMapOf()
            turtleTreeBuffer.getValue(turtle.y)
        }
        turtleRow[turtle.x].let {
            if (it != null && it !== turtle)
                throw Exception("don't override your kind (at x == ${turtle.x} y == ${turtle.y})")
        }
        turtleRow[turtle.x] = turtle
    }

    fun removeAt(x: Int, y: Int): Turtle {
        val turtleRow = turtleTreeBuffer[y]
            ?: throw Exception("this turtle is in another castle, not at y == $y")
        val removed = turtleRow.remove(x)
            ?: throw Exception("this turtle is in another castle, not at x == $x and y == $y")
        return removed
    }

    fun update(oldX: Int, oldY: Int, turtle: Turtle) {
        val removed = this.removeAt(oldX, oldY)
        if (removed !== turtle) throw Exception("congratulations, that was the wrong turtle")
        this.addToTree(turtle)
    }

    fun add(turtle: Turtle) {
        allListBuffer.add(turtle)
        this.addToTree(turtle)
    }

    fun pushBuffers() {
        allList = allListBuffer.toList()
        turtleTree = turtleTreeBuffer.map { it.key to it.value.toMap() }.toMap()
    }
}