package com.final_project.crowd_counting.auth.repository

import com.final_project.crowd_counting.auth.model.LoginRequest
import com.final_project.crowd_counting.auth.model.RegisterRequest
import com.final_project.crowd_counting.auth.source.IAuthApiService
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: IAuthApiService){
  suspend fun callLoginApi(loginRequest: LoginRequest) = apiService.login(loginRequest)
  suspend fun callRegisterApi(registerRequest: RegisterRequest) = apiService.register(registerRequest)
}