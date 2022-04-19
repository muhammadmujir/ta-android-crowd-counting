package com.final_project.crowd_counting.base.source.network

import android.util.Log
import com.final_project.crowd_counting.base.model.BaseApiResponse
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

class Deserializer<T>: JsonDeserializer<BaseApiResponse<T>> {
  override fun deserialize(
    json: JsonElement?,
    typeOfT: Type?,
    context: JsonDeserializationContext?
  ): BaseApiResponse<T> {
    json as JsonObject
    Log.d("Kesalahan", json.toString())
    val code = json.get("code").asInt
    val status = json.get("status").asString
    val error = json.get("error")
    val errors: Any? = when {
      error.isJsonArray -> error.asJsonArray
      error.isJsonObject -> error.asJsonObject
      else -> error.asString
    }

    return BaseApiResponse(code = code, status=status, errors = errors)
  }
}