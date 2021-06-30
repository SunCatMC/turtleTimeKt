package me.suncatmc.turtleTime

class World(val grid: Grid) {
    val turtleStorage = TurtleStorage(grid.rowSize, grid.columnSize)
    val portalStorage = PortalStorage()

    val isAwake: Boolean
        get() = turtleStorage.allList.isNotEmpty() && !turtleStorage.allList.all { it.isAsleep }

    init {
        extractFeatures()
    }

    operator fun invoke() {
        turtleStorage.movableList.forEach{ it.invoke() }
        turtleStorage.movableList.forEach{ it.postProcessing() }
        turtleStorage.freshlyAdded.forEach{ it.postProcessing() }
        turtleStorage.pushBuffers()

    }

    private fun extractFeatures() {
        grid.forEachIndexed { y, row ->
            row.forEachIndexed { x, c ->
                if (c in CodeGroup.turtles) {
                    turtleStorage.add(Turtle(x, y, this))
                    grid[x, y] = CodeGroup.mapTurtleToCell.getValue(c)
                } else if (c in CodeGroup.portals) {
                    portalStorage.add(c, x, y)
                }
            }
        }
        turtleStorage.pushBuffers()
        portalStorage.pushTree()
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