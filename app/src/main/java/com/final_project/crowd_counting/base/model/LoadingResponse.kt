package com.final_project.crowd_counting.base.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

data class LoadingResponse(
  var isLoading: Boolean = false,
  var message: String = "",
  var retryCallback: Queue<() -> Unit>? = null,
  var startLoading: ((Int) -> Unit)? = null
)
