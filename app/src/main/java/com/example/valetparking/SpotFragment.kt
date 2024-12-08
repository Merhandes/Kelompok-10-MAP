package com.example.valetparking

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SpotFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var dateTimeTextView: TextView
    private var parkingSpotId: String = "" // Initialize parking spot ID
    private var isFilled: Boolean = false // Track if the spot is filled
    private val handler = Handler(Looper.getMainLooper())
    private val ADD_EDIT_REQUEST_CODE = 1 // Define request code for AddEditActivity

    // Initialize UI elements in onCreateView instead of onCreate
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment's layout
        val view = inflater.inflate(R.layout.fragment_spot, container, false)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize UI elements
        val btnEdit1: TextView = view.findViewById(R.id.etEdit1)
        val carImage1: ImageView = view.findViewById(R.id.carImage1)
        val editText1: TextView = view.findViewById(R.id.etEdit1)
        val incrementButton1: Button = view.findViewById(R.id.incrementButton1)

        // Initialize TextView for real-time update
        dateTimeTextView = view.findViewById(R.id.tv_date_time_edit)

        // Start real-time date and time update
        startRealTimeUpdate()

        btnEdit1.setOnClickListener {
            // Launch AddEditActivity
            activity?.let {
                startActivityForResult(Intent(it, AddEditActivity::class.java).apply {
                    putExtra("PARKING_SPOT_ID", parkingSpotId) // Pass the parking spot ID if needed
                }, ADD_EDIT_REQUEST_CODE)
            }
        }

        incrementButton1.setOnClickListener {
            if (isFilled) {
                // If already filled, update to not filled
                updateParkingSpot(false)
            } else {
                // If not filled, go to AddEditActivity
                activity?.let {
                    startActivityForResult(Intent(it, AddEditActivity::class.java).apply {
                        putExtra("PARKING_SPOT_ID", parkingSpotId) // Pass the parking spot ID
                    }, ADD_EDIT_REQUEST_CODE)
                }
            }
        }

        // Load the first unfilled parking spot from Firestore
        loadFirstUnfilledParkingSpot()

        return view
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
                    Toast.makeText(requireContext(), "No unfilled parking spots available", Toast.LENGTH_SHORT).show()
                    isFilled = false // Treat as unfilled if no spots available
                    updateViewVisibility() // Update UI accordingly
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load parking spots", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateParkingSpot(filled: Boolean) {
        firestore.collection("parkingSpots").document(parkingSpotId)
            .update("filled", filled)
            .addOnSuccessListener {
                isFilled = filled // Update local filled state
                updateViewVisibility() // Update the UI accordingly
                Toast.makeText(requireContext(), "Parking spot updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update parking spot", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateViewVisibility() {
        // UI elements inside the fragment's layout
        val view = view ?: return
        val carImage1: ImageView = view.findViewById(R.id.carImage1)
        val editText1: TextView = view.findViewById(R.id.etEdit1)
        val incrementButton1: Button = view.findViewById(R.id.incrementButton1)

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

    private fun startRealTimeUpdate() {
        val runnable = object : Runnable {
            override fun run() {
                // Format time as needed
                val currentTime = Calendar.getInstance().time
                val dateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.getDefault())
                val formattedTime = dateFormat.format(currentTime)

                // Set time to TextView
                dateTimeTextView.text = formattedTime

                // Update TextView every second
                handler.postDelayed(this, 1000)
            }
        }

        // Start the first update
        handler.post(runnable)
    }

    // Handle the result from AddEditActivity
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
