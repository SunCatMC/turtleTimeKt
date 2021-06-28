package me.suncatmc.turtleTime

data class Grid(private val grid: MutableList<Row> = mutableListOf()): List<Row> by grid {

    val rowSize: Int
        get() = grid.first().size

    val columnSize: Int
        get() = grid.size

    fun addRow(rowStr: String) {
        if (grid.isNotEmpty() && rowStr.length != rowSize) {
            throw WrongRowSizeException(columnSize, rowStr.length, rowSize)
        }
        grid.add(Row(rowStr.toCharArray().toMutableList()))
    }

    override fun toString(): String {
        return "Grid$grid"
    }

    val mergedRows: List<String>
        get() = grid.map { it.joinToString(separator = "") }

    fun copy(): Grid {
        return Grid(grid.map { it.copy() }.toMutableList())
    }

    operator fun get(x: Int, y: Int): Char {
        return grid[y.mod(columnSize)][x.mod(rowSize)]
    }

    operator fun set(x: Int, y: Int, ch: Char) {
        grid[y.mod(columnSize)][x.mod(rowSize)] = ch
    }
}