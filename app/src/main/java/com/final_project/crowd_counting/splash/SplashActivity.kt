package com.final_project.crowd_counting.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.final_project.crowd_counting.MainActivity
import com.final_project.crowd_counting.R
import com.final_project.crowd_counting.auth.AuthenticationActivity
import com.final_project.crowd_counting.base.view.CrowdCountingApp
import java.util.*
import kotlin.concurrent.schedule

class SplashActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)
    animateView()
    checkToken()
  }

  private fun checkToken() {
    Timer("SettingUp", false).schedule(3000) {
      val userToken = (applicationContext as CrowdCountingApp).userToken
      if (userToken.isNullOrEmpty()){
        startActivity(Intent(this@SplashActivity, AuthenticationActivity::class.java))
      } else {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
      }
    }
  }

  private fun animateView() {
    val fadeAnim = ObjectAnimator.ofFloat(findViewById<ImageView>(R.id.iv_logo), "alpha", 0f, 1f).apply {
      duration = 1000
    }
    val rotate1 = ObjectAnimator.ofFloat(findViewById<ImageView>(R.id.iv_top), "rotation", 20f, 60f).apply {
      duration = 1000
    }
    val rotate2 = ObjectAnimator.ofFloat(findViewById<ImageView>(R.id.iv_bottom), "rotation", 20f, 0f).apply {
      duration = 1000
    }
    AnimatorSet().apply {
      play(rotate1).with(rotate2)
      play(fadeAnim).after(rotate2)
      start()
    }
  }
}