package com.example.valetparking

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.valetparking.R

class CameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_qr)

        // Button Generate QR
        val generateQRButton: Button = findViewById(R.id.generateQRButton)
        generateQRButton.setOnClickListener {
            // Tambahkan logika untuk generate QR Code
            generateQRCode()
        }

        // Button Done
        val doneButton: Button = findViewById(R.id.doneButton)
        doneButton.setOnClickListener {
            // Tambahkan logika navigasi atau aksi selesai
            finish()
        }
    }

    private fun generateQRCode() {
        // Logika untuk menghasilkan QR Code
        Toast.makeText(this, "QR Code Generated!", Toast.LENGTH_SHORT).show()
    }
}