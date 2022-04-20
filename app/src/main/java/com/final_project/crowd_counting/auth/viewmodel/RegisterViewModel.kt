package com.final_project.crowd_counting.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.final_project.crowd_counting.auth.model.RegisterRequest
import com.final_project.crowd_counting.auth.model.RegisterResponse
import com.final_project.crowd_counting.auth.repository.AuthRepository
import com.final_project.crowd_counting.base.model.BaseApiResponse
import com.final_project.crowd_counting.base.source.network.ApiCallHelper.safeApiCall
import com.final_project.crowd_counting.base.source.network.ResponseWrapper
import com.final_project.crowd_counting.base.view.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authRepository: AuthRepository): BaseViewModel() {
  private val _registerResponse = MutableLiveData<ResponseWrapper<BaseApiResponse<RegisterResponse>>>()
  val registerResponse: LiveData<ResponseWrapper<BaseApiResponse<RegisterResponse>>> get() = _registerResponse

  fun register(registerRequest: RegisterRequest){
    viewModelScope.launch(Dispatchers.IO) {
      val result = safeApiCall { authRepository.callRegisterApi(registerRequest) }
      _registerResponse.postValue(result)
    }
  }
}