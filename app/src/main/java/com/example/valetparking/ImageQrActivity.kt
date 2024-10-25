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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ImageQrActivity : AppCompatActivity() {
    private lateinit var currentTimeTextView: TextView
    private val storage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()


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
            val imageView = findViewById<ImageView>(R.id.imageView)
            imageView.setImageURI(selectedImage)
            findViewById<Button>(R.id.uploadButton).visibility = View.GONE

            selectedImage?.let { uri ->
                val storageRef = storage.reference.child("images/${UUID.randomUUID()}.jpg")
                storageRef.putFile(uri).addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        saveImageUrlToFirestore(downloadUrl.toString())
                    }
                }.addOnFailureListener {
                }
            }
        }
    }

    private fun saveImageUrlToFirestore(url: String) {
        val imageInfo = hashMapOf("imageUrl" to url, "timestamp" to System.currentTimeMillis())
        db.collection("uploads").add(imageInfo).addOnSuccessListener {
        }.addOnFailureListener {
        }
    }


    private fun updateCurrentTime() {
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        currentTimeTextView.text = currentTime

        currentTimeTextView.postDelayed({ updateCurrentTime() }, 1000)
    }
}
