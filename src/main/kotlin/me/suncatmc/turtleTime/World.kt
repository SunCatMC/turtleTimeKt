package me.suncatmc.turtleTime

class World(val grid: Grid) {
    val turtleStorage = TurtleStorage(grid.rowSize, grid.columnSize)
    //TODO: add sequence object for reading input

    val isAwake: Boolean
        get() = turtleStorage.allList.isNotEmpty() && turtleStorage.allList.none { it.isAsleep }

    init {
        extractTurtles()
    }

    operator fun invoke() {
        turtleStorage.movableList.forEach{ it.invoke() }
    }

    private fun extractTurtles() {
        grid.forEachIndexed { y, row ->
            row.forEachIndexed { x, c ->
                if (c in CodeGroup.turtles) {
                    turtleStorage.add(Turtle(x, y, this))
                    grid[x, y] = CodeGroup.mapTurtleToCell.getValue(c)
                }
            }
        }
    }

    val gridWithTurtles: Grid
        get() {
            return grid.copy().apply {
                turtleStorage.allList.forEach {
                    this[it.x, it.y] = CodeGroup.mapCellToTurtle[grid[it.x, it.y]] ?: CodeUnit.TURTLE_MAIN
                }
            }
        }
}