package com.final_project.crowd_counting.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.final_project.crowd_counting.base.model.BaseApiResponse
import com.final_project.crowd_counting.base.model.Event
import com.final_project.crowd_counting.base.model.User
import com.final_project.crowd_counting.base.source.network.ResponseWrapper
import com.final_project.crowd_counting.base.view.BaseViewModel
import com.final_project.crowd_counting.profile.model.UpdatePasswordRequest
import com.final_project.crowd_counting.profile.model.UpdateProfileRequest
import com.final_project.crowd_counting.profile.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repository: UserRepository): BaseViewModel(){
  private val _updateUser = MutableLiveData<Event<Pair<Int, ResponseWrapper<BaseApiResponse<User>>>>>()
  val updateUser: LiveData<Event<Pair<Int, ResponseWrapper<BaseApiResponse<User>>>>> get() = _updateUser
  private val _updatePicture = MutableLiveData<Event<ResponseWrapper<BaseApiResponse<String>>>>()
  val updatePicture: LiveData<Event<ResponseWrapper<BaseApiResponse<String>>>> get() = _updatePicture

  fun getCurrentUser(){
    launchApiCall { repository.getCurrentUser() }.let {
      viewModelScope.launch(Dispatchers.IO) {
        it.await()?.let {
          _updateUser.postValue(Event(Pair(0, it)))
        }
      }
    }
  }

  fun updateProfile(updateProfileRequest: UpdateProfileRequest){
    launchApiCall { repository.updateProfile(updateProfileRequest) }.let {
      viewModelScope.launch(Dispatchers.IO) {
        it.await()?.let {
          _updateUser.postValue(Event(Pair(1, it)))
        }
      }
    }
  }

  fun updatePassword(updatePasswordRequest: UpdatePasswordRequest){
    launchApiCall { repository.updatePassword(updatePasswordRequest) }.let {
      viewModelScope.launch(Dispatchers.IO) {
        it.await()?.let {
          _updateUser.postValue(Event(Pair(2, it)))
        }
      }
    }
  }

  fun uploadImage(file: MultipartBody.Part){
    launchApiCall { repository.uploadPicture(file) }.let { defer ->
      viewModelScope.launch(Dispatchers.IO) {
        defer.await()?.let { _updatePicture.postValue(Event(it)) }
      }
    }
  }

}
