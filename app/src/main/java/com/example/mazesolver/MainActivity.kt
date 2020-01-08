package com.example.mazesolver

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val dimensionSize = 20
    private val tag = "check_tag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gameBoard = Array(dimensionSize) { IntArray(dimensionSize) }

        Log.d(tag, findPossibleMoves(0,0).toString())

    }

    private fun findPossibleMoves(x: Int, y: Int): List<Pair<Int, Int>> {
        val possibleMoves = mutableListOf<Pair<Int, Int>>()
        if (x + 1 != dimensionSize)
            possibleMoves += Pair(x + 1, y)
        if (x - 1 != -1)
            possibleMoves += Pair(x - 1, y)
        if (y + 1 != dimensionSize)
            possibleMoves += Pair(x, y + 1)
        if (y - 1 != -1)
            possibleMoves += Pair(x, y - 1)

        return possibleMoves
    }



}
