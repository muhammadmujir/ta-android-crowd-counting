package com.final_project.crowd_counting.statistic.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StatisticRequest(
  @SerializedName("start")
  val startDate: String,
  @SerializedName("end")
  val endDate: String
): Parcelable
