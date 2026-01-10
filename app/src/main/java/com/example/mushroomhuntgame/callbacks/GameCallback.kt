package com.example.mushroomhuntgame.callbacks

interface GameCallback {
    fun updateView(row: Int, lane: Int, objID: Int, isVisible: Boolean)
    fun isViewVisible(row: Int, lane: Int): Boolean
    fun updateScore(newScore : Int)
    fun onCrush(goodObj: Boolean,  row: Int, lane: Int)
    fun updateHeart(heartIndex: Int)
    fun handleGameOver()

}