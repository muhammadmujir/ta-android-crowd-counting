package com.final_project.crowd_counting.base.source.network

import android.util.Log
import com.final_project.crowd_counting.base.model.BaseApiResponse
import com.final_project.crowd_counting.base.utils.Util.orDefaultBool
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Response

object ApiCallHelper{
  suspend fun <T> safeApiCall(apiCall: suspend () -> Response<BaseApiResponse<T>>): ResponseWrapper<BaseApiResponse<T>>? {
    return try {
      val response = apiCall()
      if (response.isSuccessful) {
        ResponseWrapper.success(response.body()!!)
      } else {
        val errorBody: BaseApiResponse<T> = parseError(response.errorBody()!!)
        if (errorBody.code == 401){
          if (errorBody.errors?.getOrNull(0)?.contains("token", true).orDefaultBool(false))
            return ResponseWrapper.error(data = errorBody.run {
              BaseApiResponse(
                code,
                status,
                data,
                listOf("Terdapat masalah autentikasi, harap logout dan login kembali"),
                pagination
              )
            })
        }
        ResponseWrapper.error(data = errorBody)
      }
    } catch (throwable: Throwable) {
      ResponseWrapper.error(data = BaseApiResponse(errors = listOf(throwable.message ?: throwable.toString())))
    }
  }

  private fun <T> parseError(response: ResponseBody?): BaseApiResponse<T> {
    try {
      response?.let { errorBody ->
        val gSON = GsonBuilder().registerTypeAdapter(BaseApiResponse<T>()::class.java, Deserializer<T>()).create()
        return gSON.fromJson<BaseApiResponse<T>>(errorBody.string(), BaseApiResponse<T>()::class.java)
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return BaseApiResponse()
  }

  fun parseError(json: String): ErrorResponse {
    return Gson().fromJson(json, ErrorResponse::class.java)
  }
}