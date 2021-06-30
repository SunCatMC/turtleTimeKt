package me.suncatmc.turtleTime

object TurtleIO {
    private val outBuilder = StringBuilder()
    val output: String
        get() = outBuilder.toString()
    fun addOutputChar(ch: Char) {
        outBuilder.append(ch)
    }
    fun flushOutput(): String {
        val out = output
        outBuilder.clear()
        return out
    }

    var input: String = ""
        private set
    var inputIndex = 0
        private set
    var isEOF = false
        private set

    tailrec fun getInputChar(): Char? {
        if (isEOF) return null
        if (inputIndex < input.length) {
            val ch = input[inputIndex]
            inputIndex++
            return ch
        }
        flushInput()
        return getInputChar()
    }
    fun flushInput() {
        if (DebugStorage.isDebug) {
            println("Input a line here: ")
        } else {
            print("\n>")
        }
        inputIndex = 0
        input = readLine() ?: run {
            isEOF = true
            input = ""
            return
        }
        input += '\n'
    }
}