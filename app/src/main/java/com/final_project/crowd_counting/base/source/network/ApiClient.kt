package com.final_project.crowd_counting.base.source.network

import android.content.Context
import com.final_project.crowd_counting.base.constant.Constant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

  fun getRetrofitInstance(context: Context, connectTimeout: Long? = null, readTimeout: Long? = null): Retrofit {
    return Retrofit.Builder()
      .baseUrl(Constant.BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .client(okhttpClient(context, connectTimeout, readTimeout))
      .build()
  }

  private fun okhttpClient(context: Context, connectTimeout: Long? = null, readTimeout: Long? = null): OkHttpClient {
    val builder = OkHttpClient.Builder()
    connectTimeout?.let { builder.connectTimeout(it, TimeUnit.MILLISECONDS) }
    readTimeout?.let { builder.readTimeout(it, TimeUnit.MILLISECONDS) }
    return builder.addInterceptor(AuthInterceptor(context)).build()
  }

}