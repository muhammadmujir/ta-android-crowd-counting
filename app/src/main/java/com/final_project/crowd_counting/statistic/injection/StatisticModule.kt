package com.final_project.crowd_counting.statistic.injection

import com.final_project.crowd_counting.base.injection.DefaultTimingOkHttpClient
import com.final_project.crowd_counting.statistic.source.IStatisticApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
class StatisticModule {
  @Provides
  fun provideStatisticService(@DefaultTimingOkHttpClient retrofit: Retrofit): IStatisticApiService {
    return retrofit.create(IStatisticApiService::class.java)
  }
}