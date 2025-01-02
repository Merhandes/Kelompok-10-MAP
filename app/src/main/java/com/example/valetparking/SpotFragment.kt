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

class SpotFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var parkingLayout: LinearLayout
    private lateinit var spotCountTextView: TextView
    private val filledSpots = mutableSetOf<Int>() // Keep track of filled spots

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_spot, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        parkingLayout = view.findViewById(R.id.parkingLayout)
        spotCountTextView = view.findViewById(R.id.spotCount)

        // Observe the parking spot count
        sharedViewModel.parkingSpot.observe(viewLifecycleOwner, { count ->
            updateParkingLayout(count)
        })

        return view
    }

    /**
     * Updates the parking layout dynamically based on the parking spot count.
     */
    private fun updateParkingLayout(spotCount: Int) {
        // Clear existing views
        parkingLayout.removeAllViews()

        // Add TextViews dynamically for each parking spot
        for (i in 1..spotCount) {
            val spotView = createParkingSpotView(i)
            parkingLayout.addView(spotView)
        }

        // Update available spots count
        updateAvailableSpots()
    }

    /**
     * Creates a TextView for a parking spot.
     */
    private fun createParkingSpotView(spotNumber: Int): TextView {
        val textView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 0) // Add margin between spots
            }
            text = "Spot $spotNumber"
            textSize = 18f
            setPadding(16, 16, 16, 16)
            setBackgroundResource(if (filledSpots.contains(spotNumber)) R.color.green else R.color.red)
            gravity = android.view.Gravity.CENTER

            // Click listener to navigate to AddEditActivity
            setOnClickListener {
                if (filledSpots.contains(spotNumber)) {
                    Toast.makeText(context, "Spot $spotNumber is already filled!", Toast.LENGTH_SHORT).show()
                } else {
                    filledSpots.add(spotNumber) // Mark the spot as filled
                    setBackgroundResource(R.color.green) // Update color
                    updateAvailableSpots() // Recalculate available spots

                    // Navigate to AddEditActivity
                    val intent = Intent(requireContext(), AddEditActivity::class.java)
                    intent.putExtra("spotNumber", spotNumber)
                    startActivity(intent)
                }
            }
        }

        return textView
    }

    /**
     * Updates the available spots count dynamically.
     */
    private fun updateAvailableSpots() {
        val totalSpots = sharedViewModel.parkingSpot.value ?: 0
        val availableSpots = totalSpots - filledSpots.size
        spotCountTextView.text = availableSpots.toString()
    }
}
