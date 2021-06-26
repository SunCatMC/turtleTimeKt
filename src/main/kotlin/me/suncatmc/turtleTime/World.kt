package me.suncatmc.turtleTime

class World(val grid: Grid) {
    private val turtles = mutableListOf<Turtle>()

    init {
        extractTurtles()
    }

    private fun extractTurtles() {
        grid.forEachIndexed { y, row ->
            row.forEachIndexed { x, c ->
                if (c in turtleTypes) {
                    turtles.add(Turtle(x, y))
                    grid[x, y] = mapTurtleToCell.getValue(c)
                }
            }
        }
    }

    val gridWithTurtles: Grid
        get() {
            return grid.copy().apply {
                turtles.forEach {
                    this[it.x, it.y] = mapCellToTurtle[grid[it.x, it.y]] ?: '#'
                }
            }
        }

    companion object {
        val mapTurtleToCell = mapOf('O' to '#', 'o' to '~', '!' to '_')
        val mapCellToTurtle = mapTurtleToCell.entries.associateBy({ it.value }) { it.key }
        val turtleTypes = mapTurtleToCell.keys
    }
}