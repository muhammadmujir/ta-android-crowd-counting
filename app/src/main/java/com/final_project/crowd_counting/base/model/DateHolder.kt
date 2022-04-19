package com.final_project.crowd_counting.base.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DateHolder(
  var year: Int = 0,
  var month: Int = 0,
  var dayOfMonth: Int = 0,
  var hourOfDay: Int = 0,
  var minute: Int = 0
): Parcelable