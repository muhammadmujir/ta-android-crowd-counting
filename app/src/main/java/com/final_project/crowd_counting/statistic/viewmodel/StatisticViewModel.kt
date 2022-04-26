package com.final_project.crowd_counting.statistic.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.final_project.crowd_counting.base.model.BaseApiResponse
import com.final_project.crowd_counting.base.model.Camera
import com.final_project.crowd_counting.base.source.network.ResponseWrapper
import com.final_project.crowd_counting.base.view.BaseViewModel
import com.final_project.crowd_counting.statistic.model.Statistic
import com.final_project.crowd_counting.statistic.model.StatisticRequest
import com.final_project.crowd_counting.statistic.repository.StatisticRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(private val repository: StatisticRepository): BaseViewModel() {
  private val _camera = MutableLiveData<ResponseWrapper<BaseApiResponse<List<Camera>>>>()
  val camera: LiveData<ResponseWrapper<BaseApiResponse<List<Camera>>>> get() = _camera
  private val _statistic = MutableLiveData<ResponseWrapper<BaseApiResponse<List<Statistic>>>>()
  val statistic: LiveData<ResponseWrapper<BaseApiResponse<List<Statistic>>>> get() = _statistic

  init {
    getCameraList()
  }

  private fun getCameraList(){
    launchApiCall { repository.getCameraList() }.let {
      viewModelScope.launch(Dispatchers.IO) {
        _camera.postValue(it.await())
      }
    }
  }

  fun getStatistic(cameraId: Int, statisticRequest: StatisticRequest){
    launchApiCall { repository.getStatistic(cameraId, statisticRequest) }.let {
      viewModelScope.launch(Dispatchers.IO) {
        _statistic.postValue(it.await())
      }
    }
  }
}