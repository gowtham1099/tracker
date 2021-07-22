package com.hackfrenzy.tracker.network

import com.hackfrenzy.tracker.pojo.ResponseMessage
import retrofit2.http.*

interface Api {

    @POST("orderTracking")
    suspend fun startTrip(@Body data: HashMap<String, String>): ResponseMessage

    @GET("orderTracking")
    suspend fun startTracking(@Query("orderId") id:String): ResponseMessage

    @PUT("orderTracking")
    suspend fun update(@Query("orderId") id:String, @Body data: HashMap<String, String>): ResponseMessage

    @DELETE("orderTracking")
    suspend fun stopTrip(@Query("orderId") id:String): ResponseMessage

}