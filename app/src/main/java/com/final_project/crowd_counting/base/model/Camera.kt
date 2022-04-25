package com.final_project.crowd_counting.base.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Camera(
  val id: Int? = null,
  val rtspAddress: String? = null,
  val location: String? = null,
  val description: String? = null,
  val area: Float = 0.0F,
  val maxCrowdCount: Int = 0
): Parcelable