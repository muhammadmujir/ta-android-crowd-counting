package com.final_project.crowd_counting.base.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaginationState(
  var totalItem: Int = 0,
  var totalPage: Int = 1,
  var currentPage: Int = 0,
  var isLastPage: Boolean = false,
  var isLoading: Boolean = false
): Parcelable