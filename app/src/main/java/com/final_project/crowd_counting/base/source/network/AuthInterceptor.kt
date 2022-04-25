package com.final_project.crowd_counting.base.source.network

import android.content.Context
import com.final_project.crowd_counting.base.view.CrowdCountingApp
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()

    if (!request.url.encodedPath.contains("login") &&
      !request.url.encodedPath.contains("register")){
      val requestBuilder = request.newBuilder()
      val token = (context.applicationContext as CrowdCountingApp).userToken
      requestBuilder.addHeader("Authorization", "Bearer $token")

      val response = chain.proceed(requestBuilder.build())
//      if (response.code == 401){
//        context.startActivity(Intent(context, AuthenticationActivity::class.java))
//      }
      return response
    }
    return chain.proceed(request)
  }

}