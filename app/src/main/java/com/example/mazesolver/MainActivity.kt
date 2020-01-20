package com.example.mazesolver

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.hypot

class MainActivity : AppCompatActivity() {
    private val dimensionSize = 20
    private val tag = "check_tag"
    private var start = Node(Status.Empty, Coordinate(1, 1), null)
    private var goal = Node(Status.Empty, Coordinate(18, 19), null)
    private val gameBoard = Array(dimensionSize) { i ->
        Array(dimensionSize) { j ->
            Node(Status.Empty, Coordinate(i, j), null)
        }
    }
    private val DFSVisitedNodes = mutableListOf<Node>()

    enum class SolutionStatus {
        Failure, Cutoff, Success
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameBoard[3][1].nodeStatus = Status.Block
        gameBoard[2][2].nodeStatus = Status.Block
//        gameBoard[7][5].nodeStatus = Status.Block
//        gameBoard[10][5].nodeStatus = Status.Block
//        gameBoard[9][12].nodeStatus = Status.Block
//        gameBoard[19][15].nodeStatus = Status.Block
//        gameBoard[12][15].nodeStatus = Status.Block
//        gameBoard[5][6].nodeStatus = Status.Block
//        findRouteByBFS()?.forEach { Log.d(tag, it.coordinate.toString()) }
//        iterativeDeepeningSearch().forEach {
//            Log.d(
//                tag,
//                "/>${it.coordinate}"
//            )
//        }
        aStarSearch().forEach { Log.d(tag, "/>${it.coordinate}") }
    }

    private fun aStarSearch(): List<Node> {
        val frontier = mutableMapOf(start to calculateFValue(start))
        while (true) {
            val bestChoice = frontier.minBy { it.value }!!.key
            frontier.remove(bestChoice)
            if (bestChoice == goal)
                return computeRoutingToStart(bestChoice).first
            Log.d(tag, "*>${bestChoice.coordinate}")
            findPossibleMoves(bestChoice).filterNot { it in frontier.keys }
                .forEach {
                    frontier += it to calculateFValue(it)
                }
        }
    }

    private fun iterativeDeepeningSearch(): List<Node> {

        fun findRouteByDepthLimitedSearch(
            node: Node,
            limit: Int
        ): Pair<SolutionStatus, List<Node>> {
            DFSVisitedNodes += (node)
//            Log.d(tag, "***********************->${DFSVisitedNodes.size}")
            when {
                isGoal(node) -> return SolutionStatus.Success to listOf(node)
                limit == 0 -> return SolutionStatus.Cutoff to listOf()
                else -> {
                    var cutoffOccurred = false
                    findPossibleMoves(node)
                        .filterNot { it in DFSVisitedNodes }
                        .forEach {
                            val result = findRouteByDepthLimitedSearch(it, limit - 1)
                            DFSVisitedNodes.removeAt(DFSVisitedNodes.lastIndex)
//                            Log.d(tag, ">${result.first} at ${it.coordinate}")
                            if (result.first == SolutionStatus.Cutoff) {
                                cutoffOccurred = true
                            } else if (result.first != SolutionStatus.Failure)
                                return result.first to listOf(it, *result.second.toTypedArray())
                        }
                    return if (cutoffOccurred) {
                        SolutionStatus.Cutoff to listOf()
                    } else {
//                        Log.d(tag, "f at ${node.coordinate}")
                        SolutionStatus.Failure to listOf()
                    }
                }
            }
        }

        repeat(40) {
            Log.d(tag, "->$it")
            val (state, result) = findRouteByDepthLimitedSearch(start, it)
            if (state == SolutionStatus.Success) {
                return result
            } else
                DFSVisitedNodes.clear()
        }
        return listOf()
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
                return computeRoutingToStart(selectNode).first
            }
            exploredNodes += selectNode
            val possibleMoves = findPossibleMoves(selectNode)
            frontier += possibleMoves.filterNot { it in exploredNodes }
                .filterNot { it in frontier }
        }
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

    private fun computeRoutingToStart(node: Node): Pair<List<Node>, Int> {
        val routeNodeToStart = mutableListOf<Node>()
        var gValue = 1
        routeNodeToStart.add(node)
        var parentNode = node.parent
        while (parentNode != null) {
            routeNodeToStart.add(parentNode)
            parentNode = parentNode.parent
            gValue++
        }
        return routeNodeToStart to gValue
    }

    private fun calculateFValue(node: Node): Double {
        return calculateHeuristicsValue(node) + computeRoutingToStart(node).second
    }

    private fun calculateHeuristicsValue(node: Node): Double {
        return hypot(
            (node.coordinate.first - goal.coordinate.first).toDouble(),
            (node.coordinate.second - node.coordinate.second).toDouble()
        )
    }


}
