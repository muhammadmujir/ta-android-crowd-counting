package com.final_project.crowd_counting.profile.source

import com.final_project.crowd_counting.base.model.BaseApiResponse
import com.final_project.crowd_counting.base.model.User
import com.final_project.crowd_counting.profile.model.UpdatePasswordRequest
import com.final_project.crowd_counting.profile.model.UpdateProfileRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface IUserApiService {
  @GET("users/_currentUser")
  suspend fun getCurrentUser(): Response<BaseApiResponse<User>>

  @PUT("users/_currentUser")
  suspend fun updateProfile(@Body updateProfileRequest: UpdateProfileRequest): Response<BaseApiResponse<User>>

  @PUT("users/_currentUser")
  suspend fun updatePassword(@Body updatePasswordRequest: UpdatePasswordRequest): Response<BaseApiResponse<User>>

  @Multipart
  @PUT("static/images/users/_currentUser")
  suspend fun uploadPicture(@Part file: MultipartBody.Part): Response<BaseApiResponse<String>>

}