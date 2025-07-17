package com.example.navigationguide.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.navigationguide.ApiService

object RetrofitClient {
    private const val BASE_URL = "http://192.168.100.39:8080/"


    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}