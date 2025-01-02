package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ReceiptActivity : AppCompatActivity() {

    private var parkingSpotId: String = "" // Placeholder for parking spot ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)

        // Get parking spot ID and payment time from intent
        parkingSpotId = intent.getStringExtra("PARKING_SPOT_ID") ?: ""
        val paymentTime = intent.getStringExtra("paymentTime")

        // Find UI elements
        val costTextView = findViewById<TextView>(R.id.costTextView)
        val timeTextView = findViewById<TextView>(R.id.timeTextView)
        val dateTextView = findViewById<TextView>(R.id.dateTextView)
        val btnBackToSpot = findViewById<Button>(R.id.btn_back_to_spot)

        // Set UI content
        costTextView.text = "Rp 100.000"
        timeTextView.text = "Jam: $paymentTime"
        dateTextView.text =
            "Tanggal: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}"

        // Set Back To Spot button functionality
        btnBackToSpot.setOnClickListener {
            navigateBackToSpot()
        }
    }

    private fun navigateBackToSpot() {
        if (parkingSpotId.isNotEmpty()) {
            val firestore = FirebaseFirestore.getInstance()

            // Delete parking spot data from Firestore
            firestore.collection("parkingSpots").document(parkingSpotId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Mobil berhasil dihapus dari spot $parkingSpotId!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate back to SpotFragment
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("navigate_to_fragment", "SpotFragment")
                    intent.putExtra("clear_spot_id", parkingSpotId) // Pass the cleared spot ID
                    startActivity(intent)
                    finish() // Close ReceiptActivity
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal menghapus data spot!", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Parking spot ID tidak ditemukan!", Toast.LENGTH_SHORT).show()
        }
    }

}