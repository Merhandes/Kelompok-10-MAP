package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class HomeActivity : AppCompatActivity() {
    private var parkingSpot = 0
    private lateinit var firestore: FirebaseFirestore
    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var spotNumberText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize UI elements
        spotNumberText = findViewById(R.id.spotNumber)
        val decrementButton: Button = findViewById(R.id.decrementButton)
        val incrementButton: Button = findViewById(R.id.incrementButton)
        val viewParkingButton: Button = findViewById(R.id.viewParkingButton)
        val qrButton: Button = findViewById(R.id.qrButton)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Load available parking spots
        loadAvailableSpots()

        // Increment parking spot
        incrementButton.setOnClickListener {
            addParkingSpot()
        }

        // Decrement parking spot
        decrementButton.setOnClickListener {
            removeParkingSpot()
        }

        viewParkingButton.setOnClickListener {
            val intent = Intent(this, SpotActivity::class.java)
            startActivity(intent)
        }

        // QR button
        qrButton.setOnClickListener {
            Toast.makeText(this, "Generate QR for Spot: $parkingSpot", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadAvailableSpots() {
        // Query Firestore to count how many spots are unfilled (filled == false)
        listenerRegistration = firestore.collection("parkingSpots")
            .whereEqualTo("filled", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this@HomeActivity, "Failed to load spots", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                // Check if snapshot is null or empty
                if (snapshot == null || snapshot.isEmpty) {
                    parkingSpot = 0 // Set parkingSpot to 0 if no spots are found
                    updateSpotNumber() // Update the UI to reflect 0 spots
                    return@addSnapshotListener
                }

                // Count unfilled (available) parking spots
                val availableSpots = snapshot.size()  // Get the count of documents where filled == false

                // Set parkingSpot to the count of available spots
                parkingSpot = availableSpots
                updateSpotNumber()
            }
    }

    private fun addParkingSpot() {
        // Check the total number of parking spots first
        firestore.collection("parkingSpots")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.size() >= 10) {
                    Toast.makeText(this, "Max number of parking spots reached!", Toast.LENGTH_SHORT).show()
                } else {
                    // Create a new parking spot with "filled" set to false if the limit is not exceeded
                    val newSpotData = hashMapOf("filled" to false)
                    firestore.collection("parkingSpots")
                        .add(newSpotData)
                        .addOnSuccessListener { documentReference ->
                            updateSpotNumber()
                            Toast.makeText(this, "New parking spot created with ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to create parking spot: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error checking parking spots: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeParkingSpot() {
        // Find the last unfilled parking spot (filled == false)
        firestore.collection("parkingSpots")
            .whereEqualTo("filled", false)  // Query for spots where 'filled' is false
            .orderBy("__name__", com.google.firebase.firestore.Query.Direction.DESCENDING)  // Order by document ID in descending order
            .limit(1)  // Limit to the last available unfilled spot
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No unfilled spots available to delete
                    parkingSpot = 0 // Update parking spot count to 0
                    updateSpotNumber() // Update the UI to reflect no spots
                    Toast.makeText(this, "No spots available to delete", Toast.LENGTH_SHORT).show()
                } else {
                    // Get the last unfilled parking spot
                    val document = documents.documents[0]
                    firestore.collection("parkingSpots").document(document.id)
                        .delete()  // Delete the document
                        .addOnSuccessListener {
                            parkingSpot-- // Decrement the local counter
                            if (parkingSpot < 0) parkingSpot = 0 // Ensure it doesn't go below 0
                            updateSpotNumber() // Update the displayed spot count
                            Toast.makeText(this, "Parking spot deleted!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to delete parking spot", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load unfilled spots", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateSpotNumber() {
        spotNumberText.text = parkingSpot.toString() // Update the UI with the current count
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove() // Unregister the listener to avoid memory leaks
    }
}
