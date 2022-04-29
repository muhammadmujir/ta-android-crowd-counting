package com.final_project.crowd_counting.profile.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UpdateProfileRequest(
  val email: String,
  val name: String
): Parcelable