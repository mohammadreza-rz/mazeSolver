package com.example.mazesolver

enum class Status(val backgroundColor: Int) {
    Block(R.color.colorPrimary), Empty(R.color.colorAccent), Agent(R.color.colorPrimaryDark)
}

typealias Coordinate = Pair<Int, Int>

class Node(nodeStatus: Status, val coordinate: Coordinate, val parent: Node?) {

    var nodeStatus = nodeStatus
        set(value) {
            field = value
            nodeBackgroundColor = value.backgroundColor
        }

    var nodeBackgroundColor: Int = -1

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (coordinate != other.coordinate) return false

        return true
    }

    override fun hashCode(): Int {
        return coordinate.hashCode()
    }


}