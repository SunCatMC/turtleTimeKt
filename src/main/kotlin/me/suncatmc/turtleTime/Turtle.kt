package me.suncatmc.turtleTime

class Turtle(x: Int, y: Int, private val world: World) {
    var x = x
        private set(value) {
            field = value.mod(world.grid.rowSize)
        }
    var y = y
        private set(value) {
            field = value.mod(world.grid.columnSize)
        }
    var xy: Coordinates
        get() = Coordinates(x, y)
        set(value) {
            x = value.x
            y = value.y
        }
    var value = 0
        private set
    var isAsleep = false
    var hasMoved = false
        private set

    private var currentDirection = randomDirection()
    private fun randomDirection() = Direction.straightList.random()

    private val canFallAsleep: Boolean
        get() = !isAsleep && currentCharBelow !in CodeGroup.water_like
    private val isSliding: Boolean
        get() = currentCharBelow in CodeGroup.ice_like
    val currentCharBelow: Char
        get() = world.grid[xy]

    operator fun invoke() {
        when (currentCharBelow) {
            in CodeGroup.walls -> throw Exception("turtle in a wall wut (at x == $x, y == $y)")
            in CodeGroup.duplicators -> {
                dupe()
                return
            }
            CodeUnit.CONDITION -> if (value != 0) move(currentDirection)
        }
        hasMoved = move(currentDirection)
        rightAfterMove()
    }

    private fun rightAfterMove() {
        hasMoved = true
        if (currentCharBelow in CodeGroup.houses) enterHouse()
    }

    fun postProcessing() {
        if (hasMoved) {
            updateValue()
        }
        hasMoved = false
        if (isAsleep) return
        if (!isSliding) {
            currentDirection = randomDirection()
        }
    }

    private fun move(direction: Direction, isCausedByConveyor:Boolean = false): Boolean {
        if (!isAsleep || isCausedByConveyor) {
            val xy = getMovementCoordinates(direction)
            val isBlocked = isPositionBlocked(xy)
            if (!isBlocked) {
                updatePosition(xy)
                return true
            }
            if (canFallAsleep && (world.grid[xy] != CodeUnit.SOFT_WALL || isSliding)) {
                isAsleep = true
            }
        }
        if (!isCausedByConveyor) {
            return getPushedByConveyorIfNeeded()
        }
        return false
    }

    fun isPositionBlocked(xy: Coordinates): Boolean {
        val (x, y) = xy
        val ch = world.grid[xy]
        val isThereOtherTurtle = world.turtleStorage[x, y, TurtleTime.FUTURE].let {it != null && it !== this}
        return ch in CodeGroup.walls || isThereOtherTurtle
    }

    private fun dupe() {
        val positions = CodeGroup.mapDuplicatorToDirections.getValue(currentCharBelow).map {
            getMovementCoordinates(it)
        }.toSet().filter {
            !isPositionBlocked(it)
        }.shuffled()
        if (positions.isNotEmpty()) {
            if (isPositionBlocked(positions.first())) throw Exception("wait, position at ${positions.first()} is obstructed??")
            val copyTurtle = this.copy()
            updatePosition(positions.first())
            rightAfterMove()
            if (positions.size > 1) {
                if (copyTurtle.isPositionBlocked(positions.last())) throw Exception("wait, position at ${positions.last()} is obstructed??")
                world.turtleStorage.add(copyTurtle)
                copyTurtle.updatePosition(positions.last())
                copyTurtle.rightAfterMove()
            }
        } else {
            isAsleep = true
        }
    }

    private fun copy(): Turtle {
        return Turtle(x, y, world).let {
            it.value = this.value
            it.currentDirection = this.currentDirection
            it.isAsleep = this.isAsleep
            it
        }
    }
    
    private fun updateValue() {
        when (currentCharBelow) {
            in CodeGroup.constants -> {
                this.value = currentCharBelow.digitToInt(16)
            }
            in CodeGroup.inputs -> {
                readInput()
            }
        }
        if (isAsleep) return
        if (currentCharBelow in CodeGroup.math) {
            val mathOp = operatorFromChar(currentCharBelow)
            Direction.validList.map {
                getMovementCoordinates(it)
            }.forEach {
                val turtle = world.turtleStorage[it.x, it.y, TurtleTime.FUTURE]
                if (turtle != null) {
                    value = mathOp(value, turtle.value)
                }
            }
        }
    }

    private fun charToInt(ch: Char) = ch.code
    private fun charToDigit(ch: Char) = if (ch in CodeGroup.constants) {
        ch.digitToInt(CodeGroup.constants.size)
    } else {
        charToInt(ch)
    }
    private fun intToChar(i: Int) = i.toChar()
    private fun digitToChar(i: Int) = if (i in CodeGroup.constants.indices)
        i.digitToChar(CodeGroup.constants.size)
    else
        intToChar(i)

    private fun enterHouse() {
        val ch = when (currentCharBelow) {
            CodeUnit.HOUSE_CH -> intToChar(value)
            CodeUnit.HOUSE_NUM -> digitToChar(value)
            else -> throw Exception("there is no house here (at x == $x, y == $y), turtle is sad =(")
        }
        TurtleIO.addOutputChar(ch)
        world.turtleStorage.remove(this)
    }

    private fun readInput() {
        val ch = TurtleIO.getInputChar() ?: run {
            value = 0
            return
        }
        value = when (currentCharBelow) {
            CodeUnit.INPUT_CH -> charToInt(ch)
            CodeUnit.INPUT_NUM -> charToDigit(ch)
            else -> throw Exception("there is no input here (at x == $x, y == $y), turtle is sad =(")
        }
    }

    private fun updatePosition(xy: Coordinates) {
        val (oldX, oldY) = this.xy
        this.xy = xy
        world.turtleStorage.update(oldX, oldY,this)
    }

    private fun getMovementCoordinates(direction: Direction): Coordinates {
        return world.grid.mapCoordinatesToGrid(Coordinates(this.x + direction.x, this.y + direction.y))
    }

    private fun getPushedByConveyorIfNeeded(): Boolean {
        if (currentCharBelow !in CodeGroup.conveyors) return false
        return this.move(CodeGroup.mapConveyorToDirection.getValue(currentCharBelow), true)
    }

    override fun toString(): String {
        return "Turtle(xy=$xy, value=$value, isAsleep=$isAsleep, currentDirection=$currentDirection," +
                "canFallAsleep=$canFallAsleep, isSliding=$isSliding, currentCharBelow=\'$currentCharBelow\')"
    }
}

private fun operatorFromChar(charOperator: Char):(Int, Int)->Int
{
    return when(charOperator)
    {
        CodeUnit.PLUS -> {a,b->a+b}
        CodeUnit.MINUS -> {a,b->a-b}
        CodeUnit.DIV -> {a,b->a/b}
        CodeUnit.MUL -> {a,b->a*b}
        CodeUnit.MOD -> {a,b->a.mod(b)}
        else -> throw Exception("That's not a supported operator")
    }
}