package com.final_project.crowd_counting.profile.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdatePasswordRequest(
  val oldPassword: String,
  val newPassword: String
): Parcelable
