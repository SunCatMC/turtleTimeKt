package me.suncatmc.turtleTime

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.optional
import java.io.File

fun initGrid(fileName: String?): Grid {
    val grid = Grid()

    if (fileName == null) {
        println("Please input your rectangular grid. Blank line finishes the input")
        val input = generateSequence(::readLine)
        for (line in input) {
            if (line.isBlank()) {
                break
            }
            grid.addRow(line.trim())
        }
        println("Input of code finished\n")
    } else {
        File(fileName).forEachLine { line ->
            grid.addRow(line.trim())
        }
    }

    return grid
}

fun main(args: Array<String>) {
    val parser = ArgParser("turtleTime")
    val fileName by parser.argument(ArgType.String, description = "Program file").optional()

    println("Turtle Time interpreter\n")
    parser.parse(args)

    val grid = initGrid(fileName)
    println("Original grid: $grid\n")
    val world = World(grid)
    while (world.isAwake) {
    //repeat (2) {
        printDebug(world)
        println("execute!")
        world.invoke()
    }
    println("The program finished execution. Here's the finished state:")
    printDebug(world)
}

fun printDebug(world: World) {
    println("Grid without turtles: ${world.grid}")
    println("Grid with turtles: ${world.gridWithTurtles}")
    println()
}