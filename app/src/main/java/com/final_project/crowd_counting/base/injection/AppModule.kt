package com.final_project.crowd_counting.base.injection

import android.content.Context
import com.final_project.crowd_counting.base.constant.Constant
import com.final_project.crowd_counting.base.source.network.AuthInterceptor
import com.final_project.crowd_counting.base.source.network.NetworkConnectionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

  @DefaultTimingOkHttpClient
  @Provides
  fun provideDefaultRetrofit(@DefaultTimingOkHttpClient okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .baseUrl(Constant.BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .client(okHttpClient)
      .build()
  }

  @LongTimingOkHttpClient
  @Provides
  fun provideLongTimingRetrofit(@LongTimingOkHttpClient okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .baseUrl(Constant.BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .client(okHttpClient)
      .build()
  }

  @DefaultTimingOkHttpClient
  @Provides
  fun provideDefaultTimingOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(AuthInterceptor(context))
      .addInterceptor(NetworkConnectionInterceptor(context))
      .build()
  }

  @LongTimingOkHttpClient
  @Provides
  fun provideLongTimingOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(AuthInterceptor(context))
      .addInterceptor(NetworkConnectionInterceptor(context))
      .connectTimeout(1000*60*5, TimeUnit.MILLISECONDS)
      .readTimeout(1000*60*5, TimeUnit.MILLISECONDS)
      .build()
  }

//  @Singleton
//  @Provides
//  fun provideDatabase(@ApplicationContext appContext: Context) = AppDB.getDatabase(appContext)
}

@Qualifier
annotation class DefaultTimingOkHttpClient

@Qualifier
annotation class LongTimingOkHttpClient