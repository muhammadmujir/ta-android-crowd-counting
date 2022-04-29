package com.final_project.crowd_counting.profile.injection

import com.final_project.crowd_counting.base.injection.DefaultTimingOkHttpClient
import com.final_project.crowd_counting.profile.source.IUserApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
class UserModule {
  @Provides
  fun provideUserService(@DefaultTimingOkHttpClient retrofit: Retrofit): IUserApiService {
    return retrofit.create(IUserApiService::class.java)
  }
}