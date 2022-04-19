package com.final_project.crowd_counting.base.source.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ErrorResponse(
  @SerializedName("error")
  val error: String? = null,
  @SerializedName("message")
  val message: String? = null
): Parcelable