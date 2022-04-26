package com.final_project.crowd_counting.statistic.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Statistic(
  val timestamp: String? = null,
  val crowdCount: Int = 0
): Parcelable