package com.final_project.crowd_counting.base.source.network

import android.content.Context
import android.util.Log
import com.final_project.crowd_counting.base.utils.Util.isInternetAvailable
import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
  context: Context,
  crossinline query: () -> Flow<ResultType>,
  crossinline fetch: suspend () -> RequestType,
  crossinline saveFetchResult: suspend (RequestType) -> Unit,
  crossinline onFetchFailed: (Throwable) -> Unit = { },
  crossinline shouldFetch: (ResultType) -> Boolean = { isInternetAvailable(context) }
) = flow {

  val data = query().first()
  val flow = if (shouldFetch(data)) {
    Log.d("ShouldFetch", "YES")
    try {
      saveFetchResult(fetch())
      query().map { ResponseWrapper.success(it) }
    } catch (throwable: Throwable) {
      onFetchFailed(throwable)
      query().map { ResponseWrapper.error(throwable.message, it) }
    }
  } else {
    Log.d("ShouldFetch", "NO")
    query().map { ResponseWrapper.success(it) }
  }
  emitAll(flow)
}