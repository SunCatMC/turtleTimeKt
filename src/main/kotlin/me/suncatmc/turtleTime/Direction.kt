package me.suncatmc.turtleTime

enum class Direction(val x: Int, val y: Int) {
    NONE(0, 0),
    LEFT(-1, 0), RIGHT(1, 0), UP(0, -1), DOWN(0, 1),
    LEFT_UP(-1, -1), LEFT_DOWN(-1, 1), RIGHT_UP(1, -1), RIGHT_DOWN(1, 1);

    operator fun plus(other: Direction) = values().firstOrNull {
        (it.x == this.x + other.x) && (it.y == this.y + other.y)
    } ?: NONE

    companion object {
        val straightList = listOf(LEFT, RIGHT, UP, DOWN)
        val diagonalList = listOf(LEFT_UP, LEFT_DOWN, RIGHT_UP, RIGHT_DOWN)
        val validList = straightList + diagonalList
    }
}