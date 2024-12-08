package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class HomeFragment : Fragment() {

    private var parkingSpot = 0
    private lateinit var firestore: FirebaseFirestore
    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var spotNumberText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize UI elements
        spotNumberText = rootView.findViewById(R.id.spotNumber)
        val decrementButton: Button = rootView.findViewById(R.id.decrementButton)
        val incrementButton: Button = rootView.findViewById(R.id.incrementButton)
        val qrButton: Button = rootView.findViewById(R.id.qrButton)

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

        // QR button
        qrButton.setOnClickListener {
            val intent = Intent(requireContext(), ImageQrActivity::class.java)
            startActivity(intent)
        }

        return rootView
    }

    private fun loadAvailableSpots() {
        // Query Firestore to count how many spots are unfilled (filled == false)
        listenerRegistration = firestore.collection("parkingSpots")
            .whereEqualTo("filled", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Failed to load spots", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "Max number of parking spots reached!", Toast.LENGTH_SHORT).show()
                } else {
                    // Create a new parking spot with "filled" set to false if the limit is not exceeded
                    val newSpotData = hashMapOf("filled" to false)
                    firestore.collection("parkingSpots")
                        .add(newSpotData)
                        .addOnSuccessListener { documentReference ->
                            updateSpotNumber()
                            Toast.makeText(requireContext(), "New parking spot created with ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed to create parking spot: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error checking parking spots: ${e.message}", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "No spots available to delete", Toast.LENGTH_SHORT).show()
                } else {
                    // Get the last unfilled parking spot
                    val document = documents.documents[0]
                    firestore.collection("parkingSpots").document(document.id)
                        .delete()  // Delete the document
                        .addOnSuccessListener {
                            parkingSpot-- // Decrement the local counter
                            if (parkingSpot < 0) parkingSpot = 0 // Ensure it doesn't go below 0
                            updateSpotNumber() // Update the displayed spot count
                            Toast.makeText(requireContext(), "Parking spot deleted!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to delete parking spot", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load unfilled spots", Toast.LENGTH_SHORT).show()
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
