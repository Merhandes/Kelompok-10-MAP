package com.example.valetparking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        val incrementButton: Button = view.findViewById(R.id.incrementButton)
        val decrementButton: Button = view.findViewById(R.id.decrementButton)
        val spotNumberTextView: TextView = view.findViewById(R.id.spotNumber)

        // Retrieve the user's UID
        val userId = auth.currentUser?.uid

        // Load the saved spot number from Firestore
        userId?.let {
            db.collection("users")
                .document(it)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val savedSpotNumber = document.getLong("spotNumber")?.toInt() ?: 0
                        sharedViewModel.setParkingSpot(savedSpotNumber)
                    }
                }
        }

        // Observe changes to the parking spot number and update the UI
        sharedViewModel.parkingSpot.observe(viewLifecycleOwner) { spotNumber ->
            spotNumberTextView.text = spotNumber.toString()
        }

        // Increment the spot number
        incrementButton.setOnClickListener {
            val currentSpotNumber = sharedViewModel.parkingSpot.value ?: 0
            val newSpotNumber = currentSpotNumber + 1
            updateSpotNumberInFirestore(userId, newSpotNumber)
        }

        // Decrement the spot number
        decrementButton.setOnClickListener {
            val currentSpotNumber = sharedViewModel.parkingSpot.value ?: 0
            if (currentSpotNumber > 0) {
                val newSpotNumber = currentSpotNumber - 1
                updateSpotNumberInFirestore(userId, newSpotNumber)
            }
        }

        return view
    }

    private fun updateSpotNumberInFirestore(userId: String?, newSpotNumber: Int) {
        userId?.let {
            db.collection("users")
                .document(it)
                .update("spotNumber", newSpotNumber) // Only update the `spotNumber` field
                .addOnSuccessListener {
                    sharedViewModel.setParkingSpot(newSpotNumber) // Update the ViewModel
                }
                .addOnFailureListener {
                    // Handle the error
                }
        }
    }
}
