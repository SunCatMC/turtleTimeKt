package me.suncatmc.turtleTime

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.optional
import java.io.File
import java.util.*
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

object Project {
    private val versionProperties = Properties()
    init {
        versionProperties.load(this.javaClass.getResourceAsStream("/project.properties"))
    }

    val version: String
        get() = versionProperties.getProperty("version") ?: "no version"
}

object DebugStorage {
    var isDebug = false
    var loopCountdown = 0
    var isEOF = false
}

fun main(args: Array<String>) {
    val parser = ArgParser("turtleTime")
    val isDebug by parser.option(
        ArgType.Boolean, shortName = "d",
        description = "Turn on debug mode").default(false)
    val isQuiet by parser.option(
        ArgType.Boolean, shortName = "q",
        description = "\"Quiet mode\", aka remove the executable description string").default(false)
    val fileName by parser.argument(ArgType.String, description = "Program file").optional()

    parser.parse(args)
    if (!isQuiet) {
        println("Turtle Time interpreter, version ${Project.version}")
    }
    DebugStorage.isDebug = isDebug

    val grid = initGrid(fileName)
    val world = World(grid)
    while (world.isAwake) {
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
        world.invoke()
        val line = TurtleIO.flushOutput()
        if (line.isNotEmpty()) {
            if (DebugStorage.isDebug) {
                println("Output received: ")
            }
            print(line)
            if (DebugStorage.isDebug) {
                println()
            }
        }
    }
    println()
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
    println("List of turtles:")
    world.turtleStorage.allList.forEach {
        println(it)
    }
    val withoutTString = "Grid without turtles:"
    val withTString = "Grid with turtles:"
    if (world.grid.rowSize >= 40) {
        println(withoutTString)
        world.grid.mergedRows.forEach { println(it) }
        println(withTString)
        world.gridWithTurtles.mergedRows.forEach { println(it) }
    } else {
        println("%-${world.grid.rowSize}s %s".format(withTString, withoutTString))
        world.gridWithTurtles.mergedRows.forEachIndexed { index, row ->
            println("%-${withTString.length}s %s".format(row, world.grid.mergedRows[index]))
        }
    }
    println()
}