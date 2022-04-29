package com.final_project.crowd_counting.camera.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.final_project.crowd_counting.base.model.BaseApiResponse
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.base.model.Event
import com.final_project.crowd_counting.base.source.network.ResponseWrapper
import com.final_project.crowd_counting.base.view.BaseViewModel
import com.final_project.crowd_counting.camera.model.CameraListQuery
import com.final_project.crowd_counting.camera.model.CameraRequest
import com.final_project.crowd_counting.camera.repository.CameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(private val repository: CameraRepository): BaseViewModel() {
  private val _camera = MutableLiveData<Event<Pair<Int, ResponseWrapper<BaseApiResponse<Camera>>>>>()
  val camera: LiveData<Event<Pair<Int, ResponseWrapper<BaseApiResponse<Camera>>>>> get() = _camera
  private val _cameraList = MutableLiveData<PagingData<Camera>>()
  val cameraList: LiveData<PagingData<Camera>> get() = _cameraList
  private val _updatePicture = MutableLiveData<Event<ResponseWrapper<BaseApiResponse<String>>>>()
  val updatePicture: LiveData<Event<ResponseWrapper<BaseApiResponse<String>>>> get() = _updatePicture

  fun createCamera(cameraRequest: CameraRequest) {
    launchApiCall { repository.createCamera(cameraRequest) }.let {
      viewModelScope.launch(Dispatchers.IO) {
        it.await()?.let {
          _camera.postValue(Event(Pair(CREATE_KEY, it)))
        }
      }
    }
  }

  fun updateCamera(cameraId: Int, cameraRequest: CameraRequest) {
    launchApiCall { repository.updateCamera(cameraId, cameraRequest) }.let {
      viewModelScope.launch(Dispatchers.IO) {
        it.await()?.let {
          _camera.postValue(Event(Pair(UPDATE_KEY, it)))
        }
      }
    }
  }

  fun getCameraDetail(cameraId: Int) {
    launchApiCall { repository.getCameraDetail(cameraId) }.let {
      viewModelScope.launch(Dispatchers.IO) {
        it.await()?.let {
          _camera.postValue(Event(Pair(GET_DETAIL_KEY, it)))
        }
      }
    }
  }

  fun deleteCamera(cameraId: Int) {
    launchApiCall { repository.deleteCamera(cameraId) }.let {
      viewModelScope.launch(Dispatchers.IO) {
        it.await()?.let {
          _camera.postValue(Event(Pair(DELETE_KEY, it)))
        }
      }
    }
  }

  fun getCameraListByOwner(cameraListQuery: CameraListQuery? = null) {
    launchPagingAsync(
      {
        if (cameraListQuery == null) {
          repository.getCameraListByOwner().cachedIn(viewModelScope)
        } else {
          repository.getCameraListByOwnerWithQuery(
            cameraListQuery.isActive, cameraListQuery.isPublic
          ).cachedIn(viewModelScope)
        }
      },
      {
        viewModelScope.launch(Dispatchers.IO) {
          it.collectLatest { _cameraList.postValue(it) }
        }
      }
    )
  }

  fun uploadImage(cameraId: Int, file: MultipartBody.Part){
    launchApiCall { repository.uploadPicture(cameraId,file) }.let { defer ->
      viewModelScope.launch(Dispatchers.IO) {
        defer.await()?.let { _updatePicture.postValue(Event(it)) }
      }
    }
  }

  companion object{
    const val CREATE_KEY = 1
    const val UPDATE_KEY = 2
    const val GET_DETAIL_KEY = 3
    const val DELETE_KEY = 4
  }
}