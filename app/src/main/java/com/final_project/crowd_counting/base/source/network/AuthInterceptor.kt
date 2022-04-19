package com.final_project.crowd_counting.base.source.network

import android.content.Context
import com.final_project.crowd_counting.base.injection.FastipApplication
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val requestBuilder = chain.request().newBuilder()

    if (!request.url.encodedPath.contains("login") &&
      !request.url.encodedPath.contains("register")){

      val token = (context.applicationContext as FastipApplication).userToken
      requestBuilder.addHeader("Authorization", "Bearer $token")

      val response = chain.proceed(requestBuilder.build())
      if (response.code == 403){
//        context.startActivity(Intent(context, LoginActivity::class.java))
      }
      return response
    }
    return chain.proceed(request)
  }

}