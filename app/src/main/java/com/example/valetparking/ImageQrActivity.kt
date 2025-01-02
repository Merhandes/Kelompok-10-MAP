package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class ImageQrActivity : AppCompatActivity() {
    private lateinit var currentTimeTextView: TextView
    private lateinit var qrImageView: ImageView
    private var parkingSpotId: String = "SPOT_1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_qr)

        currentTimeTextView = findViewById(R.id.currentTime)
        qrImageView = findViewById(R.id.imageView)
        val generateButton = findViewById<Button>(R.id.generateQRButton)
        val doneButton = findViewById<Button>(R.id.doneButton)

        generateButton.text = "Generate QR"
        generateButton.setOnClickListener {
            generateRandomQr()
        }

        doneButton.setOnClickListener {
            navigateToReceiptPage()
        }

        updateCurrentTime()

    }


    private fun generateRandomQr() {
        val randomData = UUID.randomUUID().toString()
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.encodeBitmap(randomData, BarcodeFormat.QR_CODE, 400, 400)
            qrImageView.setImageBitmap(bitmap)

        } catch (e: Exception) {
            e.printStackTrace()

        }

    }



    private fun navigateToReceiptPage() {
        val intent = Intent(this, ReceiptActivity::class.java)
        intent.putExtra("paymentTime", currentTimeTextView.text.toString())
        intent.putExtra("PARKING_SPOT_ID", parkingSpotId)
        startActivity(intent)
    }

    private fun updateCurrentTime() {
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        currentTimeTextView.text = currentTime

        currentTimeTextView.postDelayed({ updateCurrentTime() }, 1000)
    }
}