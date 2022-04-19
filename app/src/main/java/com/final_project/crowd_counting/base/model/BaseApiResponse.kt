package com.final_project.crowd_counting.base.model

import com.google.gson.annotations.SerializedName

data class BaseApiResponse<T>(
  @SerializedName("code")
  val code: Int = 200,
  @SerializedName("status")
  val status: String? = null,
  @SerializedName("data")
  val data: T? = null,
  @SerializedName("errors")
  val errors: Any? = null,
  @SerializedName("pagination")
  val pagination: Pagination? = null
)