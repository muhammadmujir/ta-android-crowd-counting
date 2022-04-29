package com.final_project.crowd_counting.camera.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CameraRequest(
  val rtspAddress: String,
  val location: String,
  val description: String? = null,
  val area: Float,
  val maxCrowdCount: Int,
  val isActive: Boolean = true,
  val isPublic: Boolean = false
): Parcelable