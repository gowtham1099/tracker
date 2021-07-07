package com.hackfrenzy.partify.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackfrenzy.partify.network.ApiClient
import com.hackfrenzy.partify.pojo.ResponseMessage
import kotlinx.coroutines.launch

class OrderTrackingViewModel : ViewModel() {

    private val startTrip: MutableLiveData<ResponseMessage> = MutableLiveData()
    private val startTracking: MutableLiveData<ResponseMessage> = MutableLiveData()
    private val update: MutableLiveData<ResponseMessage> = MutableLiveData()
    private val stopTrip: MutableLiveData<ResponseMessage> = MutableLiveData()

    fun setStartTrip(data: HashMap<String, String>) {
        viewModelScope.launch {
            val response = ApiClient.getClient.startTrip(data)
            startTrip.postValue(response)
        }
    }

    fun getStartTripStatus(): LiveData<ResponseMessage> {
        return startTrip
    }

    fun setUpdate(id: String, data: HashMap<String, String>) {
        viewModelScope.launch {
            val response = ApiClient.getClient.update(id, data)
            update.postValue(response)
        }
    }

    fun getSUpdateStatus(): LiveData<ResponseMessage> {
        return update
    }

    fun setStartTracking(id: String) {
        viewModelScope.launch {
            val response = ApiClient.getClient.startTracking(id)
            startTracking.postValue(response)
        }
    }

    fun getStartTrackingStatus(): LiveData<ResponseMessage> {
        return startTracking
    }

    fun setStopTrip(id: String) {
        viewModelScope.launch {
            val response = ApiClient.getClient.stopTrip(id)
            stopTrip.postValue(response)
        }
    }

    fun getStopTripStatus(): LiveData<ResponseMessage> {
        return stopTrip
    }

}