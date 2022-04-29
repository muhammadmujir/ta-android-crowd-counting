package com.final_project.crowd_counting.camera.souce

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.base.source.network.ApiCallHelper
import com.final_project.crowd_counting.base.source.network.ResponseWrapper
import com.final_project.crowd_counting.base.utils.Util.orDefaultInt
import com.final_project.crowd_counting.camera.model.CameraListQuery
import com.final_project.crowd_counting.camera.souce.ICameraApiService
import javax.inject.Inject

const val QUERY_PUBLIC_CAMERA = "publicCamera"
const val QUERY_PRIVATE_CAMERA = "privateCamera"

class CameraListDataSource @Inject constructor(
  private val apiService: ICameraApiService,
  private val operation: String,
  private val query: CameraListQuery? = null
): PagingSource<Int, Camera>() {
  override fun getRefreshKey(state: PagingState<Int, Camera>): Int? = 1

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Camera> {
    val pageNumber = params.key ?: 1
    return try {
      val response = ApiCallHelper.safeApiCall {
        if (operation == QUERY_PRIVATE_CAMERA) {
          query?.let {
            apiService.getCameraListByOwnerWithQuery(it.isActive, it.isPublic)
          } ?: run {
            apiService.getCameraListByOwner()
          }
        } else apiService.getPublicCameraList()
      }
      if (response?.status == ResponseWrapper.Status.SUCCESS){
        val pagedResponse = response.body
        val totalPage = pagedResponse?.pagination?.totalItems.orDefaultInt(0) / pagedResponse?.pagination?.itemsPerPage.orDefaultInt(1)

        var nextPageNumber: Int? = null
        if (pagedResponse?.pagination?.page != null && pagedResponse.pagination.page.orDefaultInt(1) < totalPage) {
          nextPageNumber = pageNumber+1
        }

        LoadResult.Page(
          data = pagedResponse?.data.orEmpty(),
          prevKey = null,
          nextKey = nextPageNumber
        )
      } else {
        LoadResult.Error(Exception(response?.body?.errors?.getOrNull(0).toString()))
      }
    } catch (e: Exception) {
      LoadResult.Error(e)
    }
  }
}