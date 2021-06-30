package me.suncatmc.turtleTime

class TurtleStorage(val rowSize: Int, val columnSize: Int) {
    var allList = listOf<Turtle>()
        private set
    val movableList: List<Turtle>
        get() = allList.filter {
            !it.isAsleep || it.currentCharBelow in CodeGroup.conveyors || it.currentCharBelow in CodeGroup.duplicators
        }
    val freshlyAddedBuffer = mutableListOf<Turtle>()
    val freshlyAdded: List<Turtle>
        get() = freshlyAddedBuffer.toList()
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

    fun remove(turtle: Turtle) {
        remove(turtle.x, turtle.y, turtle)
        if (!allListBuffer.remove(turtle))
            throw Exception("was this turtle ever added to the world? $turtle")
    }

    private fun remove(oldX: Int, oldY: Int, turtle: Turtle) {
        val removed = this.removeFromTree(oldX, oldY)
        if (removed !== turtle) throw Exception("congratulations, $removed was the wrong turtle (not $turtle)")
    }

    private fun removeFromTree(x: Int, y: Int): Turtle {
        val turtleRow = turtleTreeBuffer[y]
            ?: throw Exception("a turtle is in another castle, not at y == $y")
        return turtleRow.remove(x)
            ?: throw Exception("a turtle is in another castle, not at x == $x and y == $y")
    }

    fun update(oldX: Int, oldY: Int, turtle: Turtle) {
        remove(oldX, oldY, turtle)
        this.addToTree(turtle)
    }

    fun add(turtle: Turtle) {
        freshlyAddedBuffer.add(turtle)
        this.addToTree(turtle)
    }

    fun pushBuffers() {
        allListBuffer.addAll(freshlyAddedBuffer)
        freshlyAddedBuffer.clear()
        allList = allListBuffer.toList()
        turtleTree = turtleTreeBuffer.map { it.key to it.value.toMap() }.toMap()
    }
}