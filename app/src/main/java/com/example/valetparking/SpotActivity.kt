package com.example.valetparking

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SpotActivity : AppCompatActivity() {
    private var isFilled: Boolean = false // Track if the spot is filled
    private lateinit var firestore: FirebaseFirestore
    private lateinit var parkingSpotId: String // ID of the parking spot document

    private val ADD_EDIT_REQUEST_CODE = 1 // Request code for AddEditActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize UI elements
        val btnBack: TextView = findViewById(R.id.btnBack)
        val btnEdit1: TextView = findViewById(R.id.etEdit1)
        val carImage1: ImageView = findViewById(R.id.carImage1)
        val editText1: TextView = findViewById(R.id.etEdit1)
        val incrementButton1: Button = findViewById(R.id.incrementButton1)

        // Set up listeners
        btnBack.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        btnEdit1.setOnClickListener {
            // Launch AddEditActivity
            startActivityForResult(Intent(this, AddEditActivity::class.java).apply {
                putExtra("PARKING_SPOT_ID", parkingSpotId) // Pass the parking spot ID if needed
            }, ADD_EDIT_REQUEST_CODE)
        }

        incrementButton1.setOnClickListener {
            if (isFilled) {
                // If already filled, update to not filled
                updateParkingSpot(false)
            } else {
                // If not filled, go to AddEditActivity
                startActivityForResult(Intent(this, AddEditActivity::class.java).apply {
                    putExtra("PARKING_SPOT_ID", parkingSpotId) // Pass the parking spot ID
                }, ADD_EDIT_REQUEST_CODE)
            }
        }

        // Load the first unfilled parking spot from Firestore
        loadFirstUnfilledParkingSpot()
    }

    private fun loadFirstUnfilledParkingSpot() {
        firestore.collection("parkingSpots")
            .whereEqualTo("filled", false) // Find unfilled spots
            .limit(1) // Get only one
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    // Get the first unfilled parking spot
                    val document = documents.documents[0]
                    parkingSpotId = document.id // Save the document ID
                    isFilled = document.getBoolean("filled") ?: false // Update filled status
                    updateViewVisibility() // Update UI based on filled state
                } else {
                    Toast.makeText(this, "No unfilled parking spots available", Toast.LENGTH_SHORT).show()
                    isFilled = false // Treat as unfilled if no spots available
                    updateViewVisibility() // Update UI accordingly
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load parking spots", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateParkingSpot(filled: Boolean) {
        firestore.collection("parkingSpots").document(parkingSpotId)
            .update("filled", filled)
            .addOnSuccessListener {
                isFilled = filled // Update local filled state
                updateViewVisibility() // Update the UI accordingly
                Toast.makeText(this, "Parking spot updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update parking spot", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateViewVisibility() {
        val carImage1: ImageView = findViewById(R.id.carImage1)
        val editText1: TextView = findViewById(R.id.etEdit1)
        val incrementButton1: Button = findViewById(R.id.incrementButton1)

        if (isFilled) {
            carImage1.visibility = View.VISIBLE // Show the car image
            editText1.visibility = View.VISIBLE // Show the edit text
            incrementButton1.text = "-" // Change button text to "-"
        } else {
            carImage1.visibility = View.GONE // Hide the car image
            editText1.visibility = View.GONE // Hide the edit text
            incrementButton1.text = "+" // Change button text to "+"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Check if the data has the filled status
            val isFilledFromResult = data?.getBooleanExtra("IS_FILLED", false) ?: false
            if (isFilledFromResult) {
                isFilled = true // Update the local filled status
                updateViewVisibility() // Update UI
            }
        }
    }
}
