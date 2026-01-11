package com.example.mushroomhuntgame.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.VideoView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.mushroomhuntgame.game.GameProperties
import com.example.mushroomhuntgame.R
import com.example.mushroomhuntgame.databinding.ActivityStartBinding
import com.example.mushroomhuntgame.utilities.EffectManager
import com.example.mushroomhuntgame.utilities.hideSystemBars

class StartMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private lateinit var expandableContent : LinearLayout
    private lateinit var gameSpeedLayout : LinearLayout
    private lateinit var videoBackground : VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var gameType : Int = GameProperties.GAME_TYPE_BUTTONS
        var speed : Long = GameProperties.GAME_SPEED_SLOW

        videoBackground = binding.videoBackground
        EffectManager.getInstance().setupVideoBackground(videoBackground, R.raw.glowing_mushrooms)

        expandableContent = binding.expandableContent
        gameSpeedLayout = binding.layoutDifficulty
        val startButton = binding.startButton
        val modeButton = binding.btnMode
        val topTenButton = binding.topTenButton
        val speedButton = binding.switchFastSpeed
        val modeLayout = binding.layoutMode
        val imgSettings = binding.imgSettings

        modeLayout.setOnClickListener {
            if (expandableContent.visibility == View.GONE) {
                expandableContent.visibility = View.VISIBLE

                if(gameType == GameProperties.GAME_TYPE_BUTTONS)
                    gameSpeedLayout.visibility = View.VISIBLE

                imgSettings.animate().rotation(180f).setDuration(300).start()
            } else {
                expandableContent.visibility = View.GONE
                gameSpeedLayout.visibility = View.INVISIBLE
                imgSettings.animate().rotation(0f).setDuration(300).start()
            }
        }

        modeButton.setOnCheckedChangeListener {_, isChecked ->
            if (isChecked) {
                gameType = GameProperties.GAME_TYPE_SENSORS
                gameSpeedLayout.visibility = View.INVISIBLE
                speedButton.isChecked = false
            }
            else{
                gameType = GameProperties.GAME_TYPE_BUTTONS
                gameSpeedLayout.visibility = View.VISIBLE
            }
        }

        speedButton.setOnCheckedChangeListener {  _, isChecked ->
            speed = if (isChecked) {
                GameProperties.GAME_SPEED_FAST
            } else
                GameProperties.GAME_SPEED_SLOW
        }

        startButton.setOnClickListener {
            EffectManager.getInstance().stopLobbyMusic()
            startGame(gameType, speed)
        }

        topTenButton.setOnClickListener{
            EffectManager.getInstance().isInternalNavigation = true
            val intent = Intent(this, RecordsActivity::class.java)
            startActivity(intent)
        }

        //EffectManager.getInstance().playBackgroundMusic(R.raw.lobby_music)


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onRestart() {
        super.onRestart()
        //EffectManager.getInstance().playBackgroundMusic(R.raw.lobby_music)
        expandableContent.visibility = View.GONE
        gameSpeedLayout.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        EffectManager.getInstance().isInternalNavigation = false
        EffectManager.getInstance().startLobbyMusic(R.raw.lobby_music)
        videoBackground.start()
        hideSystemBars()
    }

    override fun onPause() {
        super.onPause()
        EffectManager.getInstance().pauseLobbyMusic()
    }

    private fun startGame(gameType: Int, speed : Long) {
        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(GameProperties.GAME_TYPE_KEY, gameType)
        bundle.putLong(GameProperties.GAME_SPEED_KEY, speed)
        intent.putExtra("BUNDLE", bundle)
        startActivity(intent)
    }

}