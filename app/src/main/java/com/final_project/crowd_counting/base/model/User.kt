package com.final_project.crowd_counting.base.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
  val id: String? = null,
  val email: String? = null,
  val name: String = ""
): Parcelable