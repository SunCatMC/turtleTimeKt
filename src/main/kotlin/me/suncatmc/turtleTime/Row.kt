package me.suncatmc.turtleTime

data class Row(private val list: MutableList<Char>): List<Char> by list {
    operator fun set(i: Int, ch: Char) {
        list[i] = ch
    }

    override fun toString(): String {
        return "Row$list"
    }

    fun copy(): Row {
        return Row(list.toMutableList())
    }
}