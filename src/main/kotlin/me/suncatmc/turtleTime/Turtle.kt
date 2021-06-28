package me.suncatmc.turtleTime

class Turtle(x: Int, y: Int, val world: World) {
    var x = x
        private set(value) {
            field = value.mod(world.grid.rowSize)
        }
    var y = y
        private set(value) {
            field = value.mod(world.grid.columnSize)
        }
    var value = 0
        private set

    operator fun invoke() {
        move(Direction.straightList.random())
    }

    private fun move(direction: Direction) {
        this.x += direction.x
        this.y += direction.y
    }


}