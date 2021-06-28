package me.suncatmc.turtleTime

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.optional
import java.io.File
import kotlin.system.exitProcess

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

object DebugStorage {
    var loopCountdown = 0
    var isEOF = false
}

fun main(args: Array<String>) {
    val parser = ArgParser("turtleTime")
    val isDebug by parser.option(
        ArgType.Boolean, shortName = "d",
        description = "Turn on debug mode").default(false)
    val fileName by parser.argument(ArgType.String, description = "Program file").optional()

    println("Turtle Time interpreter\n")
    parser.parse(args)

    val grid = initGrid(fileName)
    val world = World(grid)
    if (isDebug) {
        debug(world)
    }
    while (world.isAwake) {
        world.invoke()
        if (isDebug) {
            debug(world)
            with(DebugStorage) {
                if (loopCountdown > 0) {
                    loopCountdown--
                } else if (!isEOF) {
                    debugInputs()
                }
                Unit
            }
        }
    }
    if (isDebug) {
        println("The program finished execution. Here's the finished state:")
        debug(world)
    }
}

fun debugInputs() {
    println("press Enter to make a step or send a debug command\n" +
            "Available commands:\n" +
            "<number> to do this number of steps, Q to quit immediately")
    val line = readLine() ?: run {
        DebugStorage.isEOF = true
        return
    }
    if (line.startsWith('q', true)) {
        exitProcess(0)
    } else {
        line.toIntOrNull()?.let {
            DebugStorage.loopCountdown = it
        }
    }
}

fun debug(world: World) {
    println("Grid without turtles: ${world.grid}")
    println("Grid with turtles: ${world.gridWithTurtles}")
    println("List of turtles: ${world.turtleStorage.allList}")
    println()
}