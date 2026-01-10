package com.example.mushroomhuntgame.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.mushroomhuntgame.utilities.AccSensorApi
import com.example.mushroomhuntgame.utilities.AccSensorCallBack
import com.example.mushroomhuntgame.utilities.EffectManager
import com.example.mushroomhuntgame.callbacks.GameCallback
import com.example.mushroomhuntgame.game.GameManager
import com.example.mushroomhuntgame.game.GameProperties
import com.example.mushroomhuntgame.R
import com.example.mushroomhuntgame.databinding.ActivityMainBinding
import com.example.mushroomhuntgame.utilities.hideSystemBars
import android.graphics.drawable.AnimationDrawable
import android.widget.VideoView

class MainActivity : AppCompatActivity() , GameCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var accSensorApi: AccSensorApi
    private lateinit var gameManager : GameManager
    private val effectManager = EffectManager.getInstance()
    private lateinit var gameMap: Array<Array<ImageView>>
    private lateinit var heartsArray: List<ImageView>
    private lateinit var videoBackground : VideoView


    private var gameType: Int = GameProperties.GAME_TYPE_BUTTONS
    private var gameSpeed: Long = GameProperties.GAME_SPEED_SLOW


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rightBtn = binding.rightbutton
        val leftBtn = binding.leftbutton
        val bundle = intent.getBundleExtra("BUNDLE")

        videoBackground = binding.videoBackground
        EffectManager.getInstance().setupVideoBackground(videoBackground, R.raw.raining_game_background)

        gameType = bundle!!.getInt(GameProperties.GAME_TYPE_KEY)
        gameSpeed = bundle.getLong(GameProperties.GAME_SPEED_KEY)
        mapInnitUI()
        heartInnitUI()
        mainCharInnitUI()
        effectManager.playBackgroundMusic(R.raw.soundtrack)


        gameManager = GameManager(lifecycleScope, gameSpeed)
        gameManager.setListener(this)

        if (gameType == GameProperties.GAME_TYPE_SENSORS) {
            binding.buttonPanel.visibility = View.GONE
            startSensor()
        } else {
            rightBtn.setOnClickListener { gameManager.moveMainCharacter(1) }
            leftBtn.setOnClickListener { gameManager.moveMainCharacter(-1) }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        effectManager.stopBackgroundMusic()
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()

        gameManager.isPaused = false
        effectManager.resumeBackgroundMusic()
        if (gameType == GameProperties.GAME_TYPE_SENSORS)
            accSensorApi.start()
    }

    override fun onPause() {
        super.onPause()
        hideSystemBars()
        gameManager.isPaused = true
        effectManager.pauseBackgroundMusic()
        if (gameType == GameProperties.GAME_TYPE_SENSORS)
            accSensorApi.stop()
    }

    private fun mapInnitUI() {
        gameMap = arrayOf(
            arrayOf(binding.img00, binding.img10, binding.img20, binding.img30, binding.img40),
            arrayOf(binding.img01, binding.img11, binding.img21, binding.img31, binding.img41),
            arrayOf(binding.img02, binding.img12, binding.img22, binding.img32, binding.img42),
            arrayOf(binding.img03, binding.img13, binding.img23, binding.img33, binding.img43),
            arrayOf(binding.img04, binding.img14, binding.img24, binding.img34, binding.img44),
            arrayOf(binding.img05, binding.img15, binding.img25, binding.img35, binding.img45)
        )
    }

    private fun heartInnitUI() {
        heartsArray = listOf(binding.life1, binding.life2, binding.life3)
    }

    private fun mainCharInnitUI() {
        val mainCharacter = gameMap[gameMap.size - 1][gameMap[0].size /2]
        mainCharacter.setImageResource(R.drawable.wizard)
        mainCharacter.visibility = View.VISIBLE
    }

    private fun startSensor() {
        accSensorApi = AccSensorApi(this, object : AccSensorCallBack {
            override fun data(x: Float, y: Float, z: Float) {
                if (x < -2) {
                    gameManager.moveMainCharacter(1)
                }
                if (x > 2) {
                    gameManager.moveMainCharacter(-1)
                }
                if (y < -1) {
                    gameManager.setSpeed(GameProperties.GAME_SPEED_FAST)
                }
                if (y > -1) {
                    gameManager.setSpeed(GameProperties.GAME_SPEED_SLOW)
                }
            }
        })
        accSensorApi.start()
    }

    private fun showExitDialog() {
        onPause()
        AlertDialog.Builder(this)
            .setTitle("Stop Game?")
            .setMessage("Are you sure you want to exit? Your progress will be lost.")
            .setPositiveButton("Yes") { _, _ ->
                finish()
            }
            .setNegativeButton(
                "No"
            ) { _, _ ->
                onResume()
            }
            .show()
    }


    override fun handleGameOver() {

        binding.rightbutton.isEnabled = false
        binding.leftbutton.isEnabled = false

        effectManager.makeToast("Game Over!", 500)
        effectManager.stopBackgroundMusic()
        effectManager.makeVibration(300)
        effectManager.playSoundEffect(R.raw.gameover)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, ScoreActivity::class.java)
            val bundle = Bundle()
            bundle.putInt("SCORE", gameManager.getScore())
            bundle.putString("TIME", gameManager.getFinalTime())
            intent.putExtra("BUNDLE", bundle)
            startActivity(intent)
            finish()
        }, 2000)
    }

    override fun updateView(
        row: Int,
        lane: Int,
        objID: Int,
        isVisible: Boolean
    ) {
        runOnUiThread {
            val obj = gameMap[row][lane]
            if (isVisible) {
                obj.setImageResource(objID)
                obj.visibility = View.VISIBLE
                if(objID == R.drawable.wizard){
                    if (gameManager.isHurt()) {
                        effectManager.startAnimation(R.anim.blink_animation, obj, 500)}
                    }
                else{
                    (obj.drawable as? AnimationDrawable)?.start()
                }
            } else {
                obj.visibility = View.INVISIBLE
                if (objID == R.drawable.wizard)
                    effectManager.stopAnimation(obj)
            }
        }
    }

    override fun isViewVisible(row: Int, lane: Int): Boolean {
        return gameMap[row][lane].isVisible
    }


    override fun updateScore(newScore: Int) {
        binding.score.text = String.format("%02d", newScore)
    }

    override fun onCrush(goodObj: Boolean, row: Int, lane: Int) {
        if(goodObj){

            effectManager.playSoundEffect(R.raw.bonus)
        }
        else{
            val currentView = gameMap[row][lane]
            effectManager.playSoundEffect(R.raw.hurt)
            effectManager.startAnimation(R.anim.blink_animation, currentView, 500)
            effectManager.makeVibration(500)
            effectManager.makeToast("YIKES!", 100)

        }
    }

    override fun updateHeart(heartIndex: Int) {
        heartsArray[heartIndex].setImageResource(R.drawable.heart_gone)
    }

}