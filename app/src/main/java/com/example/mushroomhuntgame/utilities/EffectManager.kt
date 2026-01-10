package com.example.mushroomhuntgame.utilities

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import java.lang.ref.WeakReference
import androidx.core.net.toUri

class EffectManager private constructor(context: Context) {

    private val contextRef = WeakReference(context)

    private var backgroundMusicPlayer: MediaPlayer? = null

    companion object {

        private var instance: EffectManager? = null


        fun init(context: Context): EffectManager {
            return instance ?: synchronized(this) {
                instance ?: EffectManager(context).also { instance = it }
            }
        }

        fun getInstance(): EffectManager {
            return instance ?: throw IllegalStateException(
                "EffectManager must be initialized by calling init(context) before use."
            )
        }
    }

    fun makeVibration(duration: Long) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = contextRef.get()
                ?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            contextRef.get()?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect =
                    VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        }
    }

    fun playSoundEffect(soundResourceId: Int) {
        val context = contextRef.get() ?: return
        val mediaPlayer = MediaPlayer.create(context, soundResourceId)
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release()
        }
        mediaPlayer.start()
    }

    fun playBackgroundMusic(soundResourceId: Int) {
        backgroundMusicPlayer?.release()
        backgroundMusicPlayer = MediaPlayer.create(contextRef.get(), soundResourceId)
        backgroundMusicPlayer?.isLooping = true
        backgroundMusicPlayer?.start()
    }

    fun stopBackgroundMusic() {
        backgroundMusicPlayer?.stop()
        backgroundMusicPlayer?.release()
        backgroundMusicPlayer = null
    }

    fun pauseBackgroundMusic() {
        if (backgroundMusicPlayer?.isPlaying == true) {
            backgroundMusicPlayer?.pause()
        }
    }

    fun resumeBackgroundMusic() {
        if (backgroundMusicPlayer?.isPlaying == false) {
            backgroundMusicPlayer?.start()
        }
    }


    fun makeToast(message: String, duration: Int) {
        Toast.makeText(contextRef.get(), message, duration).show()
    }

    fun startAnimation(animationResourceId: Int, view: ImageView, delay: Long) {
        val animation = AnimationUtils.loadAnimation(contextRef.get(), animationResourceId)
        view.startAnimation(animation)
        Handler(Looper.getMainLooper()).postDelayed({
            view.clearAnimation()
        }, delay)
    }

    fun stopAnimation(view: ImageView) {
        view.clearAnimation()
    }

    fun setupVideoBackground(view: VideoView, videoResourceId: Int) {
        val context = contextRef.get() ?: return
        val uri = "android.resource://${context.packageName}/${videoResourceId}".toUri()

        view.setVideoURI(uri)
        view.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.setVolume(0f, 0f)
        }
        view.start()
    }
}