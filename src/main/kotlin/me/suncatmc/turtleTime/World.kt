package me.suncatmc.turtleTime

class World(val grid: Grid) {
    private val turtles = mutableListOf<Turtle>()
    private val movableTurtles = turtles
    //TODO: add sequence object for reading input

    val isAwake: Boolean
        get() = turtles.isNotEmpty() && turtles.none { it.isAsleep }

    init {
        extractTurtles()
    }

    operator fun invoke() {
        movableTurtles.forEach{ it.invoke() }
    }

    private fun extractTurtles() {
        grid.forEachIndexed { y, row ->
            row.forEachIndexed { x, c ->
                if (c in CodeGroup.turtles) {
                    turtles.add(Turtle(x, y, this))
                    grid[x, y] = CodeGroup.mapTurtleToCell.getValue(c)
                }
            }
        }
    }

    val gridWithTurtles: Grid
        get() {
            return grid.copy().apply {
                turtles.forEach {
                    this[it.x, it.y] = CodeGroup.mapCellToTurtle[grid[it.x, it.y]] ?: CodeUnit.TURTLE_MAIN
                }
            }
        }
}