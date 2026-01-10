package com.example.mushroomhuntgame.activities

import android.R
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
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




//    private fun startCircularExit(targetActivity: Class<*>) {
//        val rootLayout = binding.splashScreen
//
//        // Get the center of the screen
//        val cx = rootLayout.width / 2
//        val cy = rootLayout.height / 2
//
//        // Get the initial radius of the screen
//        val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
//
//        // Create the circular shrink animation
//        val anim = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, initialRadius, 0f)
//        anim.duration = 800
//
//        anim.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator) {
//                rootLayout.visibility = View.INVISIBLE
//
//                // Start the next activity
//                val intent = Intent(this@SplashScreenActivity, targetActivity)
//                startActivity(intent)
//
//                // Use a simple fade to bridge the gap
//                overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out)
//                finish()
//            }
//        })
//        anim.start()
//    }


}