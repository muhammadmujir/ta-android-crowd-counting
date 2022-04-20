package com.final_project.crowd_counting.auth.source

import com.final_project.crowd_counting.auth.model.LoginRequest
import com.final_project.crowd_counting.auth.model.LoginResponse
import com.final_project.crowd_counting.auth.model.RegisterRequest
import com.final_project.crowd_counting.auth.model.RegisterResponse
import com.final_project.crowd_counting.base.model.BaseApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IAuthApiService {
  @POST("users/_login")
  suspend fun login(
    @Body loginRequest: LoginRequest? = null
  ): Response<BaseApiResponse<LoginResponse>>

  @POST("users/_register")
  suspend fun register(@Body registerRequest: RegisterRequest): Response<BaseApiResponse<RegisterResponse>>
}