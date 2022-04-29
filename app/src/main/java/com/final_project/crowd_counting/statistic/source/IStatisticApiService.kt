package com.final_project.crowd_counting.statistic.source

import com.final_project.crowd_counting.base.model.BaseApiResponse
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.statistic.model.Statistic
import com.final_project.crowd_counting.statistic.model.StatisticRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IStatisticApiService {

  @GET("cameras/_all")
  suspend fun getAllCameraList(): Response<BaseApiResponse<List<Camera>>>

  @POST("cameras/{cameraId}/_statistics")
  suspend fun getStatistic(
    @Path("cameraId") cameraId: Int,
    @Body statisticRequest: StatisticRequest
  ): Response<BaseApiResponse<List<Statistic>>>
}