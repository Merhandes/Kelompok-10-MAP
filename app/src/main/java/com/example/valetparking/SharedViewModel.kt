package com.example.valetparking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _parkingSpot = MutableLiveData(0)
    val parkingSpot: LiveData<Int> get() = _parkingSpot

    private val _availableSpots = MutableLiveData(0)
    val availableSpots: LiveData<Int> get() = _availableSpots

    fun setParkingSpot(count: Int) {
        _parkingSpot.value = count
        _availableSpots.value = count // Initially all are available
    }

    fun setAvailableSpots(count: Int) {
        _availableSpots.value = count
    }
}
