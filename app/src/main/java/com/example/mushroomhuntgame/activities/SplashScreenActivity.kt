package com.example.mushroomhuntgame.activities

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.mushroomhuntgame.R
import com.example.mushroomhuntgame.databinding.ActivitySplashScreenBinding
import com.example.mushroomhuntgame.utilities.hideSystemBars

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemBars()

        val progressBar = binding.loadingProgress
        startAnimation(progressBar)

    }

    private fun startAnimation(progressBar: ProgressBar) {
        val progressAnimator = ValueAnimator.ofInt(0, 100)

        progressAnimator.duration = 4000
        progressAnimator.interpolator = LinearInterpolator()

        progressAnimator.addUpdateListener { visualProgress ->
            val animatedValue = visualProgress.animatedValue as Int
            progressBar.progress = animatedValue

            if (animatedValue == 100) {
                startIntentWithFade()
            }
        }
        progressAnimator.start()

    }
    private fun startIntentWithFade() {

        val intent = Intent(this, StartMenuActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

}