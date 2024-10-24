package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SpotActivity : AppCompatActivity() {
    private var isFilled: Boolean = false // Track if the spot is filled
    private lateinit var firestore: FirebaseFirestore
    private lateinit var parkingSpotId: String // ID of the parking spot document

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

        btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        btnEdit1.setOnClickListener {
            val intent = Intent(this, AddEditActivity::class.java)
            startActivity(intent)
        }

        incrementButton1.setOnClickListener {
            if (isFilled) {
                // If already filled, we want to set it to false and hide the elements
                updateParkingSpot(false)
            } else {
                // If not filled, we want to go to AddEditActivity
                val addEditIntent = Intent(this, AddEditActivity::class.java)
                startActivity(addEditIntent)
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
                // Check if the documents collection is not empty
                if (documents.size() > 0) {
                    // Get the first unfilled parking spot
                    val document = documents.documents[0]
                    parkingSpotId = document.id // Save the document ID
                    isFilled = document.getBoolean("filled") ?: false // Update filled status
                    updateViewVisibility() // Update UI based on filled state
                } else {
                    Toast.makeText(this, "No unfilled parking spots available", Toast.LENGTH_SHORT).show()
                    isFilled = false // No spots available, treat as unfilled
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
            carImage1.visibility = View.VISIBLE
            editText1.visibility = View.VISIBLE
            incrementButton1.text = "-" // Change button text to "-"
        } else {
            carImage1.visibility = View.GONE
            editText1.visibility = View.GONE
            incrementButton1.text = "+" // Change button text to "+"
        }
    }
}