package com.final_project.crowd_counting.statistic.repository

import com.final_project.crowd_counting.statistic.model.StatisticRequest
import com.final_project.crowd_counting.statistic.source.IStatisticApiService
import javax.inject.Inject

class StatisticRepository @Inject constructor(private val apiService: IStatisticApiService) {
  suspend fun getCameraList() = apiService.getAllCameraList()
  suspend fun getStatistic(cameraId: Int, statisticRequest: StatisticRequest) = apiService.getStatistic(cameraId, statisticRequest)
}