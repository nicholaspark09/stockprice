package com.cincinnatiai.marketpricelibrary.di

import androidx.annotation.VisibleForTesting
import com.cincinnatiai.marketpricelibrary.api.StockApi
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkModule private constructor(
    val url: String,
    @VisibleForTesting val apiToken: String,
    @VisibleForTesting val isDebug: Boolean = false
){

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().also {
            it.addNetworkInterceptor(
                HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).setLevel(
                    if (isDebug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                )
            )
            it.addInterceptor { chain ->
                val url = chain.request().url().newBuilder()
                    .addQueryParameter(TOKEN_KEY, apiToken)
                    .build()
                val newRequest = chain.request().newBuilder().url(url).build()
                chain.proceed(newRequest)
            }
        }.build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(okHttpClient)
            .build()
    }

    val stockApi: StockApi = retrofit.create(StockApi::class.java)

    companion object {

        private const val TOKEN_KEY = "api_token"

        @Volatile
        private var INSTANCE: NetworkModule ?= null

        @JvmStatic
        fun intialize(url: String,
                      apiToken: String,
                      isDebug: Boolean = false): NetworkModule {
            if (INSTANCE == null) {
                synchronized(NetworkModule::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = NetworkModule(url, apiToken, isDebug)
                    }
                }
            }
            return INSTANCE!!
        }

        @JvmStatic
        fun get(): NetworkModule {
            return INSTANCE ?: throw IllegalStateException("You must call initialize with the url to use this network")
        }

    }
}