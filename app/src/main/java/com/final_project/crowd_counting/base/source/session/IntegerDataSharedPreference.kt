package com.final_project.crowd_counting.base.source.session

import android.content.Context

class IntegerDataSharedPreference(context: Context): BaseSharedPreference<Int>(context) {
  override fun saveData(data: Int, key: String?) {
    prefs.edit().putInt(key, data).apply()
  }

  override fun getData(key: String?): Int {
    return prefs.getInt(key, 0)
  }

  fun incrementIntegerValue(key: String): Int{
    val incrementVal = prefs.getInt(key, 0) + 1
    saveData(incrementVal, key)
    return incrementVal
  }
}