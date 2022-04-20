package com.final_project.crowd_counting.auth.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.final_project.crowd_counting.auth.model.LoginRequest
import com.final_project.crowd_counting.auth.model.LoginResponse
import com.final_project.crowd_counting.auth.repository.AuthRepository
import com.final_project.crowd_counting.base.model.BaseApiResponse
import com.final_project.crowd_counting.base.source.network.ResponseWrapper
import com.final_project.crowd_counting.base.view.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository): BaseViewModel(){
  private val _loginResponse = MutableLiveData<ResponseWrapper<BaseApiResponse<LoginResponse>>>()
  val loginResponse: LiveData<ResponseWrapper<BaseApiResponse<LoginResponse>>>
    get() = _loginResponse

  fun callLoginApi(loginRequest: LoginRequest) {
    viewModelScope.launch(Dispatchers.IO) {
      launchApiCall { authRepository.callLoginApi(loginRequest) }.let {
        _loginResponse.postValue(it.await())
      }
    }
  }
}