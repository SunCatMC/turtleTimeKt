package me.suncatmc.turtleTime

class PortalStorage() {
    private val portalTreeBuffer = mutableMapOf<Char, MutableSet<Coordinates>>()
    lateinit var portalTree: Map<Char, Set<Coordinates>>
        private set

    fun add(ch: Char, x: Int, y: Int) {
        if (portalTreeBuffer[ch] == null) {
            portalTreeBuffer[ch] = mutableSetOf()
        }
        portalTreeBuffer.getValue(ch).add(Coordinates(x, y))
    }

    fun pushTree() {
        portalTree = portalTreeBuffer.map { (ch, set) ->
            ch to set.toSet()
        }.toMap()
    }

    fun teleportFrom(ch: Char, xy: Coordinates): Coordinates {
        val set = portalTree.getValue(ch) - xy
        return set.random()
    }
}