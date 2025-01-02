package com.example.valetparking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class HomeFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        val incrementButton: Button = view.findViewById(R.id.incrementButton)
        val decrementButton: Button = view.findViewById(R.id.decrementButton)
        val spotNumberTextView: TextView = view.findViewById(R.id.spotNumber)

        sharedViewModel.parkingSpot.observe(viewLifecycleOwner, {
            spotNumberTextView.text = it.toString()
        })

        incrementButton.setOnClickListener {
            sharedViewModel.setParkingSpot(sharedViewModel.parkingSpot.value!! + 1)
        }

        decrementButton.setOnClickListener {
            sharedViewModel.setParkingSpot(sharedViewModel.parkingSpot.value!! - 1)
        }

        return view
    }
}
