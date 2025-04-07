package com.ml.fueltrackerqr

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val TAG = "SplashActivity"
    private val SPLASH_DELAY = 1500L // 1.5 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate started")

        // Set the splash screen content view
        setContentView(R.layout.activity_splash)

        // Get references to views
        val logoBackground = findViewById<CardView>(R.id.logoBackground)
        val logoImageView = findViewById<ImageView>(R.id.splashLogo)
        val titleCard = findViewById<CardView>(R.id.titleCard)
        val subtitleCard = findViewById<CardView>(R.id.subtitleCard)

        // Load animations
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideFromLeft = AnimationUtils.loadAnimation(this, R.anim.slide_from_left)
        val slideFromRight = AnimationUtils.loadAnimation(this, R.anim.slide_from_right)

        // Apply animations
        logoBackground.startAnimation(fadeIn)
        logoImageView.startAnimation(fadeIn)
        titleCard.startAnimation(slideFromLeft)
        subtitleCard.startAnimation(slideFromRight)

        // Set a simple timer to navigate to MainActivity after delay
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                Log.d(TAG, "Preparing to launch MainActivity")
                // Create a simple intent with no extras
                val intent = Intent(this, MainActivity::class.java)
                // Add flags to create a new task
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                Log.d(TAG, "Starting MainActivity")
                startActivity(intent)
                Log.d(TAG, "MainActivity started successfully")

                // Finish this activity
                finish()
                Log.d(TAG, "SplashActivity finished")
            } catch (e: Exception) {
                Log.e(TAG, "Error launching MainActivity", e)
                // If there's an error, still finish this activity to avoid being stuck on splash
                finish()
            }
        }, SPLASH_DELAY)
    }

    override fun onPause() {
        super.onPause()
        // Ensure we finish this activity when it's paused
        finish()
    }
}
