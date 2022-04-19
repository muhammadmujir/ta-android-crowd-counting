package com.final_project.crowd_counting.base.source.session

import android.content.Context
import android.content.SharedPreferences

abstract class BaseSharedPreference<T>(context: Context){
  companion object {
    private const val SHARED_PREFERENCES = "shared_preferences"
  }
  protected val prefs: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)

  abstract fun saveData(data: T, key: String?)

  abstract fun getData(key: String?): T

  fun removeData(key: String) {
    prefs.edit().remove(key).apply()
  }

  fun clearSharedPreference() {
    prefs.edit().clear().apply()
  }
}