package com.final_project.crowd_counting.home.source

import com.final_project.crowd_counting.base.model.BaseApiResponse
import com.final_project.crowd_counting.base.model.Camera
import retrofit2.Response
import retrofit2.http.GET

interface IHomeApiService {
  @GET("cameras")
  suspend fun getCameraListByOwner(): Response<BaseApiResponse<List<Camera>>>

  @GET("cameras/_publicCamera")
  suspend fun getPublicCameraList(): Response<BaseApiResponse<List<Camera>>>
}