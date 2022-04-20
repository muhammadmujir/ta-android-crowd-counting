package com.final_project.crowd_counting.auth.injection

import com.final_project.crowd_counting.auth.source.IAuthApiService
import com.final_project.crowd_counting.base.injection.DefaultTimingOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class LoginModule {
  @Provides
  fun provideLoginService(@DefaultTimingOkHttpClient retrofit: Retrofit): IAuthApiService {
    return retrofit.create(IAuthApiService::class.java)
  }
}