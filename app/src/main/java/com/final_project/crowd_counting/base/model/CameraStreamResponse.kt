package com.final_project.crowd_counting.base.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CameraStreamResponse(
  @SerializedName("time")
  val time: Long = 0,
  @SerializedName("count")
  val count: Int = 0,
  @SerializedName("image")
  val image: String
): Parcelable
