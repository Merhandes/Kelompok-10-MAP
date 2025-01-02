package com.example.valetparking

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("parking_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val SPOT_NUMBER_KEY = "spot_number_key"
        private const val FILLED_SPOTS_KEY = "filled_spots_key"
    }

    // Save the parking spot number
    fun saveSpotNumber(spotNumber: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(SPOT_NUMBER_KEY, spotNumber)
        editor.apply()  // Apply is used for asynchronous writing
    }

    // Retrieve the parking spot number
    fun getSpotNumber(): Int {
        return sharedPreferences.getInt(SPOT_NUMBER_KEY, 0) // Default to 0 if no value is found
    }

    // Save the filled spots
    fun saveFilledSpots(filledSpots: Set<Int>) {
        val editor = sharedPreferences.edit()
        val filledSpotsString = filledSpots.joinToString(",")
        editor.putString(FILLED_SPOTS_KEY, filledSpotsString)
        editor.apply()
    }

    // Retrieve the filled spots
    fun getFilledSpots(): Set<Int> {
        val filledSpotsString = sharedPreferences.getString(FILLED_SPOTS_KEY, "")
        return if (filledSpotsString.isNullOrEmpty()) {
            emptySet() // Return an empty set if there are no filled spots
        } else {
            filledSpotsString.split(",")
                .mapNotNull { it.toIntOrNull() } // Safely convert to Int, ignoring invalid values
                .toSet()
        }
    }
}
