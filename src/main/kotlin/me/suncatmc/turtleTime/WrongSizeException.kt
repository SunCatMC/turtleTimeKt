package me.suncatmc.turtleTime

class WrongRowSizeException(index: Int, received: Int, expected: Int) : Exception("Code grid is expected to have all " +
        "rows of the same size. Row $index is supposed to be length git $expected, but instead it is length $received")