package com.final_project.crowd_counting.auth.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegisterRequest(
  val email: String,
  val password: String,
  val name: String
): Parcelable