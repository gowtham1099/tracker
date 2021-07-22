package com.hackfrenzy.tracker.pojo

import com.google.gson.annotations.SerializedName

data class ResponseMessage(
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    var data: Data
) {

    data class Data(
        @SerializedName("id")
        var id: Int,
        @SerializedName("orderId")
        val orderId: String,
        @SerializedName("latitude")
        val latitude: String,
        @SerializedName("longitude")
        val longitude: String
    )
}
