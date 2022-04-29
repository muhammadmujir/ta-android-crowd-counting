package com.final_project.crowd_counting.camera.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.camera.model.CameraListQuery
import com.final_project.crowd_counting.camera.model.CameraRequest
import com.final_project.crowd_counting.camera.souce.CameraListDataSource
import com.final_project.crowd_counting.camera.souce.ICameraApiService
import com.final_project.crowd_counting.camera.souce.QUERY_PRIVATE_CAMERA
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class CameraRepository @Inject constructor(private val apiService: ICameraApiService) {
  suspend fun createCamera(cameraRequest: CameraRequest) = apiService.createCamera(cameraRequest)
  suspend fun updateCamera(cameraId: Int, cameraRequest: CameraRequest) = apiService.updateCamera(cameraId, cameraRequest)
  suspend fun getCameraDetail(cameraId: Int) = apiService.getCameraDetail(cameraId)
  suspend fun deleteCamera(cameraId: Int) = apiService.deleteCamera(cameraId)
  fun getCameraListByOwner(): Flow<PagingData<Camera>> = Pager(
    config = PagingConfig(10, 1),
    pagingSourceFactory = { CameraListDataSource(apiService, QUERY_PRIVATE_CAMERA) },
    initialKey = 1
  ).flow
  fun getCameraListByOwnerWithQuery(active: Boolean?, public: Boolean?): Flow<PagingData<Camera>> = Pager(
    config = PagingConfig(10, 1),
    pagingSourceFactory = { CameraListDataSource(apiService, QUERY_PRIVATE_CAMERA, CameraListQuery(active, public)) },
    initialKey = 1
  ).flow
  suspend fun uploadPicture(cameraId: Int, file: MultipartBody.Part) = apiService.uploadPicture(cameraId, file)
}