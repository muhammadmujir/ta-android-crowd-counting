package com.final_project.crowd_counting.base.view

import android.app.Application
import android.util.Log
import com.final_project.crowd_counting.base.source.session.SessionManager
import com.final_project.crowd_counting.base.source.session.SessionManager.Companion.REFRESH_TOKEN
import com.final_project.crowd_counting.base.source.session.SessionManager.Companion.USER_TOKEN
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class CrowdCountingApp: Application() {
  var userToken: String? = null
  var refreshToken: String? = null

  override fun onCreate() {
    super.onCreate()
    CoroutineScope(Dispatchers.Main).launch{
      launch(Dispatchers.IO) {
        SessionManager(this@CrowdCountingApp).getData(null).let {
          userToken = it.getString(USER_TOKEN)
          Log.d("USER TOKEN", userToken.toString())
          refreshToken = it.getString(REFRESH_TOKEN)
        }
      }
    }
  }

}