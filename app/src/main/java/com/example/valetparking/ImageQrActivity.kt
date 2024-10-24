package com.example.valetparking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class ImageQrActivity : AppCompatActivity() {
    private lateinit var currentTimeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.valetparking.R.layout.activity_image_qr)

        val uploadButton = findViewById<Button>(com.example.valetparking.R.id.uploadButton)
        uploadButton.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, 3)
        }

        currentTimeTextView = findViewById(com.example.valetparking.R.id.currentTime)
        updateCurrentTime()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 3 && resultCode == RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            val imageView = findViewById<ImageView>(com.example.valetparking.R.id.imageView)
            imageView.setImageURI(selectedImage)

            // Hide the button after the image is uploaded
            val uploadButton = findViewById<Button>(com.example.valetparking.R.id.uploadButton)
            uploadButton.visibility = View.GONE
        }
    }

    private fun updateCurrentTime() {
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        currentTimeTextView.text = currentTime

        // Update the time every second
        currentTimeTextView.postDelayed({ updateCurrentTime() }, 1000)
    }
}
