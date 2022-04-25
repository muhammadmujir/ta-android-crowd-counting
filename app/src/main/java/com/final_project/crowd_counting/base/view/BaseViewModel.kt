package com.final_project.crowd_counting.base.view

import android.util.Log
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.final_project.crowd_counting.base.model.BaseApiResponse
import com.final_project.crowd_counting.base.model.LoadingResponse
import com.final_project.crowd_counting.base.source.network.Deserializer
import com.final_project.crowd_counting.base.source.network.NetworkConnectionInterceptor
import com.final_project.crowd_counting.base.source.network.ResponseWrapper
import com.final_project.crowd_counting.base.utils.Util.orDefaultBool
import com.final_project.crowd_counting.base.utils.Util.orDefaultStr
import com.final_project.crowd_counting.base.utils.custome_class.CustomeLiveData
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response
import java.util.*

abstract class BaseViewModel: ViewModel() {
  private val _loading = CustomeLiveData<LoadingResponse>()
  val loading: LiveData<LoadingResponse> get() = _loading
  private var maxIteration: Int = 0

  fun <T> launchApiCall(
    apiCall: suspend () -> Response<BaseApiResponse<T>>
  ): Deferred<ResponseWrapper<BaseApiResponse<T>>?>{

    return viewModelScope.async (Dispatchers.IO) {
      return@async try {
        Log.d("MASUK", "TRY")
        val response = apiCall()
        if (response.isSuccessful) {
          Log.d("MASUK", "ISSUCCESS")
          decreaseMaxIteration()
          ResponseWrapper.success(response.body()!!)
        } else {
          Log.d("MASUK", "ISNOT SUCCESS")
          decreaseMaxIteration()
          ResponseWrapper.error(data = parseError(response.errorBody()!!))
        }
      } catch (throwable: Throwable) {
        Log.d("MASUK", "THROW")
        if (throwable is NetworkConnectionInterceptor.NoConnectionException){
          viewModelScope.launch(Dispatchers.Main) {
            _loading.setValueWithoutNotify(
              LoadingResponse(
                _loading.value?.isLoading.orDefaultBool(false),
                throwable.message,
                _loading.value?.retryCallback?.apply {
                  add { launchApiCall { apiCall() } }
                } ?: run { LinkedList(listOf { launchApiCall { apiCall() } }) },
                ::startLoading
              )
            )
            decreaseMaxIteration()
          }
          null
        } else {
          Log.d("THROW", throwable.message.toString())
          decreaseMaxIteration()
          ResponseWrapper.error(data = BaseApiResponse(errors = listOf(throwable.message ?: throwable.toString())))
        }
      }
    }
  }

  fun <T> launchMultiApiCall(
    apiCalls: List<suspend () -> Response<BaseApiResponse<T>>>
  ): ResponseWrapper<BaseApiResponse<T>>?{
    var isSuccess = true
    var result: ResponseWrapper<BaseApiResponse<T>>? = null
    var retryList = mutableListOf<suspend ()->Response<BaseApiResponse<T>>>()
    var errorMessage = ""
    if (apiCalls.isNotEmpty()){
      startLoading(apiCalls.size)
    }
    apiCalls.forEachIndexed { index, execute ->
      val defer = viewModelScope.async (Dispatchers.IO) {
        return@async try {
          val response = execute()
          if (response.isSuccessful) {
            decreaseMaxIteration()
            ResponseWrapper.success(response.body()!!)
          } else {
            isSuccess = false
            retryList.add({ execute() })
            errorMessage = parseError<T>(response.errorBody()).toString()
            decreaseMaxIteration()
            null
          }
        } catch (throwable: Throwable) {
          isSuccess = false
          retryList.add({ execute() })
          errorMessage = throwable.message.toString()
          decreaseMaxIteration()
          null
        }
      }
      result = runBlocking(Dispatchers.IO) {
        defer.await()
      }
    }
    if (!isSuccess) {
      Log.d("multiApiCall","ERROR")
      viewModelScope.launch(Dispatchers.Main) {
        _loading.setValue(
          LoadingResponse(
            false,
            errorMessage,
            LinkedList(listOf { launchMultiApiCall(retryList) }),
            ::startLoading
          )
        )
      }
    }
    return if (isSuccess) result else null
  }

  inline fun <T> launchPagingAsync(
    crossinline execute: suspend () -> Flow<T>,
    crossinline onSuccess: (Flow<T>) -> Unit
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val result = execute()
        onSuccess(result)
      } catch (ex: Exception) {
        Log.d("GAGAL: ", ex.message.toString())
      }
    }
  }

  fun <T> parseError(response: ResponseBody?): BaseApiResponse<T> {
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

  @MainThread
  fun startLoading(maxIteration: Int = 1){
    this.maxIteration = maxIteration
    _loading.setValue(LoadingResponse(true))
  }

  fun decreaseMaxIteration(){
    maxIteration--
    Log.d("MAX ITER", maxIteration.toString())
    Log.d("CALBACK SIZE", _loading.value.toString())
    if (maxIteration == 0){
      viewModelScope.launch(Dispatchers.Main) {
        _loading.setValue(
          LoadingResponse(
            false,
            message = _loading.value?.message.orDefaultStr(""),
            retryCallback = _loading.value?.retryCallback,
            startLoading = ::startLoading
          )
        )
      }
      Log.d("LOADING FINSIHED", "YES")
    }
  }
}