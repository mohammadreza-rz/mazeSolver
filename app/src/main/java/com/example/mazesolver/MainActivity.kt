package com.example.mazesolver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val dimensionSize = 20
    private val tag = "check_tag"
    private var start = Node(Status.Empty, Coordinate(0, 0), null)
    private var goal = Node(Status.Empty, Coordinate(4, 4), null)
    private val gameBoard = Array(dimensionSize) { i ->
        Array(dimensionSize) { j ->
            Node(Status.Empty, Coordinate(i, j), null)
        }
    }
    private val frontier = mutableListOf<Node>()
    private val exploredNodes = mutableSetOf<Node>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        Log.d(tag, findPossibleMoves(0, 0).toString())
    }

    private fun findPossibleMoves(node: Node): List<Node> {
        val x = node.coordinate.first
        val y = node.coordinate.second
        val possibleMoves = mutableListOf<Node>()
        if (x + 1 != dimensionSize && gameBoard[x + 1][y].nodeStatus != Status.Block)
            possibleMoves += Node(Status.Empty, Coordinate(x + 1, y), node)
        if (y + 1 != dimensionSize && gameBoard[x][y + 1].nodeStatus != Status.Block)
            possibleMoves += Node(Status.Empty, Coordinate(x, y + 1), node)
        if (x - 1 != -1 && gameBoard[x - 1][y].nodeStatus != Status.Block)
            possibleMoves += Node(Status.Empty, Coordinate(x - 1, y), node)
        if (y - 1 != -1 && gameBoard[x][y - 1].nodeStatus != Status.Block)
            possibleMoves += Node(Status.Empty, Coordinate(x, y - 1), node)

        return possibleMoves
    }

    private fun BFSsearch(): List<Node>? {
        frontier += start
        while (true) {
            if (frontier.isEmpty())
                return null
            val selectNode = frontier[0]
            frontier.removeAt(0)
            if (isGoal(selectNode))
                computeRouting(selectNode)
            exploredNodes += selectNode
            val possibleMoves = findPossibleMoves(selectNode)
            possibleMoves.forEach {
                if (it !in exploredNodes && it !in frontier)
                    frontier.add(it)
            }
        }
    }

    private fun isGoal(node: Node): Boolean {
        val nodeCoordinate = node.coordinate
        val goalCoordinate = goal.coordinate
        if (nodeCoordinate.first == goalCoordinate.first && nodeCoordinate.second == goalCoordinate.second)
            return true
        return false
    }

    private fun computeRouting(node: Node): List<Node> {
        val routeToGoal = mutableListOf<Node>()
        routeToGoal.add(node)
        var parentNode = node.parent
        while (parentNode != null) {
            routeToGoal.add(parentNode)
            parentNode = parentNode.parent
        }
        return routeToGoal
    }


}
