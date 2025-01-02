package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SpotFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var parkingLayout: LinearLayout
    private lateinit var spotCountTextView: TextView
    private val filledSpots = mutableSetOf<Int>() // Track filled spots

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_spot, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        parkingLayout = view.findViewById(R.id.parkingLayout)
        spotCountTextView = view.findViewById(R.id.spotCount)

        // Load filled spots from Firestore
        loadFilledSpotsFromFirestore()

        // Observe parking spot count
        sharedViewModel.parkingSpot.observe(viewLifecycleOwner) { count ->
            updateParkingLayout(count)
        }

        return view
    }

    private fun updateParkingLayout(spotCount: Int) {
        parkingLayout.removeAllViews()

        for (i in 1..spotCount) {
            val spotView = createParkingSpotView(i)
            parkingLayout.addView(spotView)
        }

        updateAvailableSpots()
    }

    override fun onResume() {
        super.onResume()

        // Check if we need to clear a spot
        val clearSpotId = activity?.intent?.getStringExtra("clear_spot_id")?.toIntOrNull()
        if (clearSpotId != null && filledSpots.contains(clearSpotId)) {
            filledSpots.remove(clearSpotId)
            saveFilledSpotsToFirestore() // Save updated state to Firestore
            updateParkingLayout(sharedViewModel.parkingSpot.value ?: 0) // Refresh UI
        }
    }


    private fun createParkingSpotView(spotNumber: Int): TextView {
        val textView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 16, 0, 0) }
            text = "Spot $spotNumber"
            textSize = 18f
            setPadding(16, 16, 16, 16)
            setBackgroundResource(if (filledSpots.contains(spotNumber)) R.color.green else R.color.red)
            gravity = android.view.Gravity.CENTER

            setOnClickListener {
                val intent = Intent(requireContext(), AddEditActivity::class.java)
                intent.putExtra("PARKING_SPOT_ID", spotNumber.toString()) // Pass correct key/value
                startActivity(intent)
            }
        }

        return textView
    }

    private fun updateAvailableSpots() {
        val totalSpots = sharedViewModel.parkingSpot.value ?: 0
        val availableSpots = totalSpots - filledSpots.size
        spotCountTextView.text = availableSpots.toString()
    }

    private fun saveFilledSpotsToFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val userDoc = db.collection("users").document(userId)

            userDoc.update("filledSpots", filledSpots.toList())
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Filled spots saved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to save filled spots.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadFilledSpotsFromFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val userDoc = db.collection("users").document(userId)

            userDoc.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val spots = document.get("filledSpots") as? List<Long>
                        if (spots != null) {
                            filledSpots.clear()
                            filledSpots.addAll(spots.map { it.toInt() })
                            updateParkingLayout(sharedViewModel.parkingSpot.value ?: 0)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to load filled spots.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
