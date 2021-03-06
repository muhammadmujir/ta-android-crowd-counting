package com.final_project.crowd_counting.base.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CameraStreamRequest(
  @SerializedName("id")
  val id: Int,
  @SerializedName("rtspAddress")
  val rtspAddress: String
): Parcelable
