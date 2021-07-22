package com.hackfrenzy.tracker.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private var BASE_URL:String="https://partifysample.herokuapp.com/"

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()

    private var gson = GsonBuilder()
        .setLenient()
        .create()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)).readTimeout(
            120,
            TimeUnit.SECONDS
        )
        .connectTimeout(120, TimeUnit.SECONDS)
        .build()

    val getClient: Api
        get() {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()

            return retrofit.create(Api::class.java)
        }
}