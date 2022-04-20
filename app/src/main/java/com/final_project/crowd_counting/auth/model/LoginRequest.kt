package com.final_project.crowd_counting.auth.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginRequest(
  @field:SerializedName("email")
  val email: String? = null,
  @field:SerializedName("password")
  val password: String? = null
): Parcelable