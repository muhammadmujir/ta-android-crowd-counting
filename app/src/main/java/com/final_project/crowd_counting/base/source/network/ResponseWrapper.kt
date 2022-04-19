package com.final_project.crowd_counting.base.source.network

data class ResponseWrapper<out T>(val status: Status, val body: T?, val message: String?) {

  enum class Status {
    SUCCESS,
    ERROR
  }

  companion object {
    fun <T> success(data: T): ResponseWrapper<T> {
      return ResponseWrapper(Status.SUCCESS, data, null)
    }

    fun <T> error(message: String? = null, data: T? = null): ResponseWrapper<T> {
      return ResponseWrapper(Status.ERROR, data, message)
    }
  }
}