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
    private val filledSpots = mutableSetOf<Int>() // Track filled spots

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_spot, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        parkingLayout = view.findViewById(R.id.parkingLayout)
        spotCountTextView = view.findViewById(R.id.spotCount)

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
                if (filledSpots.contains(spotNumber)) {
                    // Spot is already filled, allow clearing
                    removeSpot(spotNumber)
                } else {
                    // Fill the spot and navigate
                    filledSpots.add(spotNumber)
                    setBackgroundResource(R.color.green)
                    updateAvailableSpots()

                    val intent = Intent(requireContext(), AddEditActivity::class.java)
                    intent.putExtra("spotNumber", spotNumber)
                    startActivity(intent)
                }
            }
        }

        return textView
    }

    private fun updateAvailableSpots() {
        val totalSpots = sharedViewModel.parkingSpot.value ?: 0
        val availableSpots = totalSpots - filledSpots.size
        spotCountTextView.text = availableSpots.toString()
    }

    private fun removeSpot(spotNumber: Int) {
        filledSpots.remove(spotNumber)
        updateParkingLayout(sharedViewModel.parkingSpot.value ?: 0)
        Toast.makeText(requireContext(), "Spot $spotNumber cleared.", Toast.LENGTH_SHORT).show()
    }
}