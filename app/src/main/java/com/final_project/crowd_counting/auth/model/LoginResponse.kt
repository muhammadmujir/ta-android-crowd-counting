package com.final_project.crowd_counting.auth.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginResponse(
  @field:SerializedName("token")
  val accessToken: String? = null,
  @field:SerializedName("refreshToken")
  val refreshToken: String? = null
): Parcelable