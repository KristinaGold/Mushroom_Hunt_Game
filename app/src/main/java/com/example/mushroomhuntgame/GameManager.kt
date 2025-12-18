package com.example.mushroomhuntgame

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.core.view.isInvisible
import com.example.mushroomhuntgame.databinding.ActivityMainBinding


class GameManager {


    var isPaused = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var map: Array<Array<ImageView>>
    private lateinit var objectList: List<Int>
    private lateinit var hearts: List<ImageView>
    private val lanes: Int = 3
    private var rows: Int = 5
    private var lives: Int = 3
    private var currentLane: Int = 1
    private lateinit var currentView: ImageView
    private var mainCharacter: Int = R.drawable.wizard
    private var isHurt = false


    fun init(activityBinding: ActivityMainBinding) {
        this.binding = activityBinding
        mapInnit()
        objInnit()
        viewInnit()
        charInnit()
        heartInnit()
    }

    private fun mapInnit() {
        map = arrayOf(
            arrayOf(binding.img00, binding.img10, binding.img20),
            arrayOf(binding.img01, binding.img11, binding.img21),
            arrayOf(binding.img02, binding.img12, binding.img22),
            arrayOf(binding.img03, binding.img13, binding.img23),
            arrayOf(binding.img04, binding.img14, binding.img24)
        )
    }

    private fun objInnit() {
        objectList = listOf(R.drawable.bad_mushroom, R.drawable.mushroom)
    }


    private fun viewInnit() {
        currentView = map[map.size - 1][currentLane]
    }

    private fun charInnit() {
        currentView.visibility = View.VISIBLE
        currentView.setImageResource(mainCharacter)
    }

    private fun heartInnit() {
        hearts = listOf(binding.life1, binding.life2, binding.life3)
    }

    fun getLanes(): Int {
        return lanes
    }

    fun getCurrentLane(): Int {
        return currentLane
    }

    fun getMap(): Array<Array<ImageView>> {
        return map
    }


    fun getObjectList(): List<Int> {
        return objectList
    }

    fun getRows(): Int {
        return rows
    }

    fun moveMainCharacter(direction: Int) {
        val newLane = currentLane + direction
        val oldView = currentView

        if (newLane in 0..<lanes) {
            currentLane = newLane
            val newView = map[rows - 1][currentLane]
            currentView = newView

            if (!newView.isInvisible)
                crush()
            placeMainCharacter(oldView, newView)
        }
    }
    fun placeMainCharacter(old: ImageView, new: ImageView) {
        EffectManager.getInstance().stopAnimation(old)
        old.visibility = View.INVISIBLE
        currentView.setImageResource(mainCharacter)
        if (isHurt) {
            EffectManager.getInstance()
                .updateAnimation(R.anim.blink_animation, old, new)
        }
        currentView.visibility = View.VISIBLE
    }

    fun crush() {
        lives--
        isHurt = true
        heartUpdate()
        EffectManager.getInstance().playSoundEffect(R.raw.hurt)
        Handler(Looper.getMainLooper()).postDelayed({
            isHurt = false
        }, 500)
        EffectManager.getInstance()
            .startAnimation(R.anim.blink_animation, currentView, 500)
        EffectManager.getInstance().makeVibration(500)
        EffectManager.getInstance().makeToast("YIKES!", 0)
        if (lives == 0) {
            restoreLives()
        }
    }


    private fun restoreLives() {
        lives = 3
        for (heart in 0..<lives)
            hearts[heart].setImageResource(R.drawable.heart_red)
    }

    private fun heartUpdate() {
        hearts[lives].setImageResource(R.drawable.heart_gone)
    }

}

