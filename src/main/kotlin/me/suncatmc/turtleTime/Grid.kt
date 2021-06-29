package me.suncatmc.turtleTime

data class Grid(private val grid: MutableList<Row> = mutableListOf()): List<Row> by grid {

    val rowSize: Int
        get() = grid.first().size

    val columnSize: Int
        get() = grid.size

    fun mapCoordinatesToGrid(xy: Coordinates): Coordinates {
        return mapCoordinatesToGrid(xy.x, xy.y)
    }

    fun mapCoordinatesToGrid(x: Int, y: Int): Coordinates {
        return Coordinates(x.mod(rowSize), y.mod(columnSize))
    }

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
        val new = mapCoordinatesToGrid(x, y)
        return grid[new.y][new.x]
    }

    operator fun get(xy: Coordinates): Char {
        val new = mapCoordinatesToGrid(xy)
        return grid[new.y][new.x]
    }

    operator fun set(x: Int, y: Int, ch: Char) {
        val new = mapCoordinatesToGrid(x, y)
        grid[new.y][new.x] = ch
    }
}