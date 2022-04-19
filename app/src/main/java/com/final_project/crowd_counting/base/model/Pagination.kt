package com.final_project.crowd_counting.base.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pagination(
  @field:SerializedName("page")
  val page: Int = 0,
  @field:SerializedName("totalItems")
  val totalItems: Int? = null,
  @field:SerializedName("itemsPerPage")
  val itemsPerPage: Int? = null
): Parcelable