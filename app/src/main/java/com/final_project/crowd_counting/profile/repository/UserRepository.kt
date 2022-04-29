package com.final_project.crowd_counting.profile.repository

import com.final_project.crowd_counting.profile.model.UpdatePasswordRequest
import com.final_project.crowd_counting.profile.model.UpdateProfileRequest
import com.final_project.crowd_counting.profile.source.IUserApiService
import okhttp3.MultipartBody
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: IUserApiService) {
  suspend fun getCurrentUser() = apiService.getCurrentUser()
  suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest) = apiService.updateProfile(updateProfileRequest)
  suspend fun updatePassword(updatePasswordRequest: UpdatePasswordRequest) = apiService.updatePassword(updatePasswordRequest)
  suspend fun uploadPicture(file: MultipartBody.Part) = apiService.uploadPicture(file)
}