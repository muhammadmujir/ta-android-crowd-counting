package com.final_project.crowd_counting.home.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.camera.souce.CameraListDataSource
import com.final_project.crowd_counting.camera.souce.ICameraApiService
import com.final_project.crowd_counting.camera.souce.QUERY_PRIVATE_CAMERA
import com.final_project.crowd_counting.camera.souce.QUERY_PUBLIC_CAMERA
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeRepository @Inject constructor(private val apiService: ICameraApiService) {
  fun getCameraListByOwner(): Flow<PagingData<Camera>> = Pager(
    config = PagingConfig(10, 1),
    pagingSourceFactory = { CameraListDataSource(apiService, QUERY_PRIVATE_CAMERA) },
    initialKey = 1
  ).flow

  fun getPublicCameraList(): Flow<PagingData<Camera>> = Pager(
    config = PagingConfig(10, 1),
    pagingSourceFactory = { CameraListDataSource(apiService, QUERY_PUBLIC_CAMERA) },
    initialKey = 1
  ).flow

}