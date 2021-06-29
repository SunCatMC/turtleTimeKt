package me.suncatmc.turtleTime

class TurtleStorage(val rowSize: Int, val columnSize: Int) {
    var allList = listOf<Turtle>()
        private set
    val movableList: List<Turtle>
        get() = allList.filter {
            !it.isAsleep || it.currentCharBelow in CodeGroup.conveyors
        }
    private val allListBuffer = mutableListOf<Turtle>()
    private var turtleTree = mapOf<Int, Map<Int, Turtle>>()
    private val turtleTreeBuffer = mutableMapOf<Int, MutableMap<Int, Turtle>>()

    operator fun get(x: Int, y: Int, time: TurtleTime): Turtle? {
        val tree = if (time == TurtleTime.FUTURE) turtleTreeBuffer else turtleTree
        return tree[y.mod(columnSize)]?.get(x.mod(columnSize))
    }

    private fun addToTree(turtle: Turtle) {
        val turtleRow = turtleTreeBuffer[turtle.y] ?: run {
            turtleTreeBuffer[turtle.y] = mutableMapOf()
            turtleTreeBuffer.getValue(turtle.y)
        }
        turtleRow[turtle.x] = turtle
    }

    fun removeAt(x: Int, y: Int) {
        val turtleRow = turtleTreeBuffer[y]
            ?: throw Exception("this turtle is in another castle, not on y == $y")
        if (turtleRow.remove(x) == null) {
            throw Exception("this turtle is in another castle, not on x == $x and y == $y")
        }
    }

    fun update(oldX: Int, oldY: Int, turtle: Turtle) {
        this.removeAt(oldX, oldY)
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