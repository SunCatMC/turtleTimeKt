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
        return "Grid[\n${grid.joinToString(separator = "\n") {
            it.joinToString(separator = "")
        }
        }]"
    }

    operator fun get(x: Int, y: Int): Char {
        return grid[y][x]
    }

    operator fun set(x: Int, y: Int, ch: Char) {
        grid[y][x] = ch
    }
}