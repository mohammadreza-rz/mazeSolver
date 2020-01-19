package com.example.mazesolver

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val dimensionSize = 20
    private val tag = "check_tag"
    private var start = Node(Status.Empty, Coordinate(1, 1), null)
    private var goal = Node(Status.Empty, Coordinate(12, 19), null)
    private val gameBoard = Array(dimensionSize) { i ->
        Array(dimensionSize) { j ->
            Node(Status.Empty, Coordinate(i, j), null)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameBoard[3][1].nodeStatus = Status.Block
        gameBoard[2][2].nodeStatus = Status.Block
        findRouteByBFS()?.forEach { Log.d(tag, it.coordinate.toString()) }
    }

    private fun findRouteByBFS(): List<Node>? {
        val frontier = mutableListOf<Node>()
        val exploredNodes = mutableSetOf<Node>()
        frontier.add(start)
        while (true) {
            if (frontier.isEmpty())
                return null
            val selectNode = frontier.removeAt(0)
            if (isGoal(selectNode)) {
                return computeRouting(selectNode)
            }
            exploredNodes += selectNode
            val possibleMoves = findPossibleMoves(selectNode)
            frontier += possibleMoves.filterNot { exploredNodes.any { hadMoved -> hadMoved.coordinate == it.coordinate } }
                .filterNot { frontier.any { willMove -> willMove.coordinate == it.coordinate } }
        }
    }

    private fun findRouteByDepthLimitedSearch(depthLimit: Int): List<Node>? {
        if (isGoal(start))
            return computeRouting(start)

        return null
    }


    private fun findPossibleMoves(node: Node): List<Node> {
        val (x, y) = node.coordinate
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

    private fun isGoal(node: Node) = node.coordinate == goal.coordinate

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
