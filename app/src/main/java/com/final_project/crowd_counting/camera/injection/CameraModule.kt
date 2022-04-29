package com.final_project.crowd_counting.camera.injection

import com.final_project.crowd_counting.base.injection.DefaultTimingOkHttpClient
import com.final_project.crowd_counting.camera.souce.ICameraApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
class CameraModule {
  @Provides
  fun provideCameraService(@DefaultTimingOkHttpClient retrofit: Retrofit): ICameraApiService {
    return retrofit.create(ICameraApiService::class.java)
  }
}