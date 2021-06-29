package me.suncatmc.turtleTime

class TurtleStorage(val rowSize: Int, val columnSize: Int) {
    private val _allList = mutableListOf<Turtle>()
    val allList: List<Turtle>
        get() = _allList
    val movableList: List<Turtle>
        get() = _allList.filter {
            !it.isAsleep || it.currentCharBelow in CodeGroup.conveyors
        }
    private val turtleTree = mutableMapOf<Int, MutableMap<Int, Turtle>>()

    operator fun get(x: Int, y: Int): Turtle? = turtleTree[y.mod(columnSize)]?.get(x.mod(columnSize))

    private fun addToTree(turtle: Turtle) {
        val turtleRow = turtleTree[turtle.y] ?: run {
            turtleTree[turtle.y] = mutableMapOf()
            turtleTree.getValue(turtle.y)
        }
        turtleRow[turtle.x] = turtle
    }

    fun removeAt(x: Int, y: Int) {
        val turtleRow = turtleTree[y]
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
        _allList.add(turtle)
        this.addToTree(turtle)
    }
}