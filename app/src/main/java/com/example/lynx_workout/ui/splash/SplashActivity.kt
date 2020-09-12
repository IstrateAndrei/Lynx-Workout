package com.example.lynx_workout.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import com.example.lynx_workout.R
import com.example.lynx_workout.ui.authentication.LoginActivity
import kotlinx.android.synthetic.main.splash_activity_layout.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity_layout)
        setAnimation()
        loginRedirect()
    }

    private fun loginRedirect() {
        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            this.finish()
        }, 3000)
    }


    private fun setAnimation() {
        val animation = AlphaAnimation(0.2f, 1.0f)
        animation.duration = 2500
        animation.fillAfter = true
        sa_icon_iv.startAnimation(animation)
    }

}