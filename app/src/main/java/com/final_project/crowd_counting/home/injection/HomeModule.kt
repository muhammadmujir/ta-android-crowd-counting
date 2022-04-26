package com.final_project.crowd_counting.home.injection

import com.final_project.crowd_counting.base.injection.DefaultTimingOkHttpClient
import com.final_project.crowd_counting.home.source.IHomeApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
class HomeModule {
  @Provides
  fun provideHomeService(@DefaultTimingOkHttpClient retrofit: Retrofit): IHomeApiService {
    return retrofit.create(IHomeApiService::class.java)
  }
}