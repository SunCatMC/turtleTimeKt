package me.suncatmc.turtleTime

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.optional

fun initWorld(fileName: String?): Grid {
    val grid = Grid()

    if (fileName == null) {
        println("Please input your rectangular grid. Blank line finishes the input")
        val input = generateSequence(::readLine)
        for (line in input) {
            if (line.isBlank()) {
                break
            }
            grid.addRow(line)
        }
        println("Code input finished")
    }

    return grid
}

fun main(args: Array<String>) {
    val parser = ArgParser("turtleTime")
    val fileName by parser.argument(ArgType.String, description = "Program file").optional()

    println("Turtle Time interpreter\n")
    parser.parse(args)

    println("test")
    val grid = initWorld(fileName)
    println(grid)
}