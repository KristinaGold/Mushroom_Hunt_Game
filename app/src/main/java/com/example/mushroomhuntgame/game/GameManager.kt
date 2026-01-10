package com.example.mushroomhuntgame.game

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.mushroomhuntgame.R
import com.example.mushroomhuntgame.callbacks.GameCallback
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameManager(lifecycleScope: LifecycleCoroutineScope, speed: Long){

    private var gameCallback: GameCallback? = null
    private var scope: LifecycleCoroutineScope = lifecycleScope
    private val goodObjects = listOf(R.drawable.anim_mushroom)
    private val badObjects = listOf(R.drawable.anim_spider)
    private val mainCharacter: Int = R.drawable.wizard
    private val LANES: Int = 5
    private val ROWS: Int = 6
    private var lives: Int = 3
    private var score: Int = 0
    private var currentLane: Int = 2
    private var isHurt = false
    private var lowestObjectID: Int = 0
    private var gameSpeed = speed
    private var gameTimer = 0
    var isPaused = false

  init {
      this.currentLane = LANES / 2

      startGameLoop()
      startGameTimer()
  }


    fun setListener(listener: GameCallback){
        this.gameCallback = listener
    }


    fun getScore(): Int {
        return this.score
    }

    fun isHurt(): Boolean {
        return this.isHurt
    }

    fun getFinalTime(): String {
        val minutes = gameTimer / 60
        val seconds = gameTimer % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun setSpeed(speed: Long) {
        this.gameSpeed = speed
    }

    private fun getObjectList(): List<Int> {
        return listOf(goodObjects, badObjects).flatten()
    }

    private suspend fun delayWhilePaused() {
        while (isPaused) {
            delay(100)
        }
    }

    private fun startGameTimer() {
        scope.launch {
            while (!isPaused) {
                delay(1000)
                if (!isPaused) {
                    gameTimer++
                    score ++
                    gameCallback?.updateScore(score)
                }
            }
        }
    }

    private fun startGameLoop() {
        scope.launch {
            while (isActive) {
                delayWhilePaused()
                spawnObj()
                delay(gameSpeed * 2)
            }
        }
    }

    private fun spawnObj() {
        val randomObjectID = getObjectList().random()
        val randomLaneIndex = (0 until LANES).random()
        gameCallback?.updateView(0, randomLaneIndex, randomObjectID, true)
        moveObj(randomLaneIndex, randomObjectID)
    }

    private fun moveObj(lane: Int, objID: Int) {
        scope.launch {
            delayWhilePaused()
            delay(gameSpeed)
            delayWhilePaused()

            for (row in 1 until ROWS) {
                delayWhilePaused()
                if (row == ROWS - 1)
                    lowestObjectID = objID

                gameCallback?.updateView(row - 1, lane, objID, false)

                if ((gameCallback?.isViewVisible(row, lane) == false)) {
                    gameCallback?.updateView(row, lane, objID, true)
                    delay(gameSpeed)
                    delayWhilePaused()

                    if (row == ROWS - 1 && (lane != currentLane)) {
                        gameCallback?.updateView(row, lane, objID, false)
                    }
                } else {
                    crush(objID, row, lane)
                    break
                }
            }
        }
    }

    fun moveMainCharacter(direction: Int) {
        val oldLane = currentLane
        val newLane = currentLane + direction

        if (newLane in 0 until LANES) {
            currentLane = newLane
            if (gameCallback?.isViewVisible(ROWS - 1, currentLane) == true)
                crush(lowestObjectID, ROWS - 1, currentLane)
            gameCallback?.updateView(ROWS - 1, oldLane, mainCharacter, false)
            gameCallback?.updateView(ROWS - 1, newLane, mainCharacter, true)

        }
    }

    private fun crush(objID: Int, row: Int, lane: Int) {
        if (goodObjects.contains(objID)) {
            score += 10
            gameCallback?.updateScore(score)
            gameCallback?.onCrush(true, row, lane)
        } else if (badObjects.contains(objID)) {
            lives--
            isHurt = true
            gameCallback?.updateHeart(lives)
            Handler(Looper.getMainLooper()).postDelayed({ isHurt = false }, 500)
            if (lives == 0) {
                scope.cancel()
                gameCallback?.handleGameOver()
                return
            }
            gameCallback?.onCrush(false, row, lane)

        }
    }
}