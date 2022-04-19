package com.final_project.crowd_counting.base.injection

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.final_project.crowd_counting.base.source.session.SessionManager
import com.final_project.crowd_counting.base.source.session.SessionManager.Companion.REFRESH_TOKEN
import com.final_project.crowd_counting.base.source.session.SessionManager.Companion.USER_TOKEN
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FastipApplication: Application(), Configuration.Provider {
  var userToken: String? = null
  var refreshToken: String? = null
  @Inject lateinit var workerFactory: HiltWorkerFactory

  override fun onCreate() {
    super.onCreate()
    CoroutineScope(Dispatchers.Main).launch{
      launch(Dispatchers.IO) {
        SessionManager(this@FastipApplication).getData(null).let {
          userToken = it.getString(USER_TOKEN)
          Log.d("USER TOKEN", userToken.toString())
          refreshToken = it.getString(REFRESH_TOKEN)
        }
      }
    }
  }

  override fun getWorkManagerConfiguration(): Configuration {
    return Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .build()
  }
}