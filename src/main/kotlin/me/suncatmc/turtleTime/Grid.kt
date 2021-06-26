package me.suncatmc.turtleTime

class Grid() {
    private val grid = mutableListOf<CharArray>()

    val rowSize: Int
        get() = grid.first().size

    val columnSize: Int
        get() = grid.size

    fun addRow(row: String) {
        if (grid.isNotEmpty() && row.length != rowSize) {
            throw WrongRowSizeException(columnSize, row.length, rowSize)
        }
        grid.add(row.toCharArray())
    }

    override fun toString(): String {
        return "Grid[\n${grid.joinToString(separator = "\n") {
            it.concatToString()
        }
        }]"
    }

    operator fun get(rowIndex: Int): CharArray {
        return grid[rowIndex]
    }

    operator fun get(x: Int, y: Int): Char {
        return grid[y][x]
    }

    operator fun set(x: Int, y: Int, ch: Char) {
        grid[y][x] = ch
    }
}