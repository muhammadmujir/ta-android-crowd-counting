package com.final_project.crowd_counting.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.base.view.BaseViewModel
import com.final_project.crowd_counting.home.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository): BaseViewModel() {
  private val _privateCameraList = MutableLiveData<PagingData<Camera>>()
  val privateCameraList: LiveData<PagingData<Camera>> get() = _privateCameraList
  private val _publicCameraList = MutableLiveData<PagingData<Camera>>()
  val publicCameraList: LiveData<PagingData<Camera>> get() = _publicCameraList

  init {
    getCameraListByOwner()
    getPublicCameraList()
  }

  private fun getCameraListByOwner(){
    launchPagingAsync(
      { repository.getCameraListByOwner().cachedIn(viewModelScope) },
      {
        viewModelScope.launch(Dispatchers.IO) {
          it.collectLatest { _privateCameraList.postValue(it) }
        }
      }
    )
  }

  private fun getPublicCameraList(){
    launchPagingAsync(
      { repository.getPublicCameraList().cachedIn(viewModelScope) },
      {
        viewModelScope.launch(Dispatchers.IO) {
          it.collectLatest { _publicCameraList.postValue(it) }
        }
      }
    )
  }

}