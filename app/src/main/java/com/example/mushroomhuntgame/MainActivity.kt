package com.example.mushroomhuntgame

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import com.example.mushroomhuntgame.databinding.ActivityMainBinding
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gameManager: GameManager
    private lateinit var map: Array<Array<ImageView>>
    private var DELAY: Long = 1000
    private var lowestObject: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        gameManager = GameManager()
        gameManager.init(binding)
        map = gameManager.getMap()
        startGameLoop()
        EffectManager.init(this)
        EffectManager.getInstance().playBackgroundMusic(R.raw.soundtrack)
        binding.rightbutton.setOnClickListener { gameManager.moveMainCharacter(1) }
        binding.leftbutton.setOnClickListener { gameManager.moveMainCharacter(-1) }


    }
    override fun onDestroy() {
        super.onDestroy()
        EffectManager.getInstance().stopBackgroundMusic()
        lifecycleScope.cancel()
    }

    override fun onResume() {
        super.onResume()
        gameManager.isPaused = false
        EffectManager.getInstance().resumeBackgroundMusic()
    }

    override fun onPause() {
        super.onPause()
        gameManager.isPaused = true
        EffectManager.getInstance().pauseBackgroundMusic()
    }

    private suspend fun delayWhilePaused() {
        while (gameManager.isPaused) {
            delay(100)
        }
    }

    fun startGameLoop() {
        lifecycleScope.launch {
            while (isActive) {
                delayWhilePaused()
                for(lane in 0..<gameManager.getLanes()){
                    if(!map[0][lane].isInvisible)
                        delay(DELAY)
                }
                spawnObj()
                delay(DELAY * 2)
            }
        }
    }


    fun spawnObj() {
        val randomObjectID = gameManager.getObjectList().random()
        val randomLaneIndex = (0..<gameManager.getLanes()).random()
        val obj = map[0][randomLaneIndex]
        obj.setImageResource(randomObjectID)
        obj.visibility = View.VISIBLE
        moveObj(randomLaneIndex, randomObjectID)
    }


    fun moveObj(lane: Int, image: Int) {
        lifecycleScope.launch {
            delayWhilePaused()
            delay(DELAY)
            delayWhilePaused()
            var currentObj: ImageView
            var nextObj: ImageView
            for (row in 1..<gameManager.getRows()) {
                delayWhilePaused()
                nextObj = map[row][lane]
                currentObj = map[row - 1][lane]

                currentObj.visibility = View.INVISIBLE
                if (nextObj.isInvisible) {
                    nextObj.setImageResource(image)
                    nextObj.visibility = View.VISIBLE
                    if (row == gameManager.getRows() - 1)
                        lowestObject = image

                    delay(DELAY)
                    delayWhilePaused()

                    if (row == gameManager.getRows() - 1 && (lane != gameManager.getCurrentLane())) {
                        nextObj.visibility = View.INVISIBLE
                    }
                } else {
                    gameManager.crush()
                    break
                }
            }
        }
    }
}