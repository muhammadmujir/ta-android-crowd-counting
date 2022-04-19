package com.final_project.crowd_counting.base.source.session

import android.content.Context
import android.os.Bundle
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SessionManager @Inject constructor(@ApplicationContext context: Context): BaseSharedPreference<Bundle>(context) {
  companion object{
    const val USER_TOKEN = "user_token"
    const val REFRESH_TOKEN = "refresh_token"
  }

  override fun saveData(data: Bundle, key: String?) {
    prefs.edit().apply{
      putString(USER_TOKEN, data.getString(USER_TOKEN))
      putString(REFRESH_TOKEN, data.getString(REFRESH_TOKEN))
      apply()
    }
  }

  override fun getData(key: String?): Bundle {
    return Bundle().apply {
      putString(USER_TOKEN, prefs.getString(USER_TOKEN, null))
      putString(REFRESH_TOKEN, prefs.getString(REFRESH_TOKEN, null))
    }
  }

}