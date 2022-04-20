package com.final_project.crowd_counting.auth.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegisterResponse(
  val id: String? = null,
  val email: String? = null,
  val name: String? = null
): Parcelable