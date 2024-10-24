package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    private var parkingSpot = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val spotNumberText: TextView = findViewById(R.id.spotNumber)
        val decrementButton: Button = findViewById(R.id.decrementButton)
        val incrementButton: Button = findViewById(R.id.incrementButton)
        val viewParkingButton: Button = findViewById(R.id.viewParkingButton)
        val qrButton: Button = findViewById(R.id.qrButton)

        // Update spot number display
        fun updateSpotNumber() {
            spotNumberText.text = parkingSpot.toString()
        }

        // Increment parking spot
        incrementButton.setOnClickListener {
            parkingSpot++
            updateSpotNumber()
        }

        // Decrement parking spot
        decrementButton.setOnClickListener {
            if (parkingSpot > 0) {
                parkingSpot--
                updateSpotNumber()
            }
        }

        viewParkingButton.setOnClickListener{
            val intent = Intent(this, SpotActivity::class.java)
            startActivity(intent)
        }

        // QR button
        qrButton.setOnClickListener {
            Toast.makeText(this, "Generate QR for Spot: $parkingSpot", Toast.LENGTH_SHORT).show()
        }
    }
}
