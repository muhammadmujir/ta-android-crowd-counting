package com.final_project.crowd_counting.camera.souce

import com.final_project.crowd_counting.base.model.BaseApiResponse
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.camera.model.CameraRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ICameraApiService {
  @POST("cameras")
  suspend fun createCamera(@Body cameraRequest: CameraRequest): Response<BaseApiResponse<Camera>>

  @PUT("cameras/{cameraId}")
  suspend fun updateCamera(@Path("cameraId") cameraId: Int, @Body cameraRequest: CameraRequest): Response<BaseApiResponse<Camera>>

  @GET("cameras/{cameraId}")
  suspend fun getCameraDetail(@Path("cameraId") cameraId: Int): Response<BaseApiResponse<Camera>>

  @GET("cameras/_publicCamera")
  suspend fun getPublicCameraList(): Response<BaseApiResponse<List<Camera>>>

  @GET("cameras")
  suspend fun getCameraListByOwner(): Response<BaseApiResponse<List<Camera>>>

  @GET("cameras")
  suspend fun getCameraListByOwnerWithQuery(
    @Query("active") active: Boolean?,
    @Query("public") public: Boolean?): Response<BaseApiResponse<List<Camera>>>

  @DELETE("cameras/{cameraId}")
  suspend fun deleteCamera(@Path("cameraId") cameraId: Int): Response<BaseApiResponse<Camera>>

  @Multipart
  @PUT("static/images/cameras/{cameraId}")
  suspend fun uploadPicture(@Path("cameraId") cameraId: Int, @Part file: MultipartBody.Part): Response<BaseApiResponse<String>>
}