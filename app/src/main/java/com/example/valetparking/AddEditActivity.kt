package com.example.valetparking

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.Manifest

class AddEditActivity : AppCompatActivity() {

    private lateinit var plateNumber: EditText
    private lateinit var color: EditText
    private lateinit var dateTimeTextView: TextView
    private lateinit var btnInsertPhoto: Button
    private lateinit var imgView: ImageView
    private lateinit var btnSave: Button // Declare btnSave here

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var selectedImageUri: Uri? = null // To hold the selected image URI
    private var parkingSpotId: String? = null // To hold the parking spot ID

    // Declare handler here
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        // Initialize Firebase instances
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        plateNumber = findViewById(R.id.et_plate_number)
        color = findViewById(R.id.et_color)
        btnInsertPhoto = findViewById(R.id.btn_insert_photo)
        imgView = findViewById(R.id.img_view)
        btnSave = findViewById(R.id.btn_save) // Initialize btnSave

        // Initialize TextView
        dateTimeTextView = findViewById(R.id.tv_date_time_edit)

        // Start real-time date and time update
        startRealTimeUpdate()

        btnInsertPhoto.isEnabled = false
        // Check camera permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            btnInsertPhoto.isEnabled = true
        }

        btnInsertPhoto.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 101)
        }

        // Save button click listener
        btnSave.setOnClickListener { // Use the initialized btnSave
            saveCarDetails()
        }
    }

    private fun startRealTimeUpdate() {
        val runnable = object : Runnable {
            override fun run() {
                // Format the current time as needed
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

    override fun onDestroy() {
        super.onDestroy()
        // Stop handler when the activity is destroyed to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            val photo: Bitmap? = data?.getParcelableExtra("data")
            imgView.setImageBitmap(photo)
            // Convert bitmap to Uri for Firebase Storage
            selectedImageUri = Uri.parse(
                MediaStore.Images.Media.insertImage(
                    contentResolver,
                    photo,
                    "Title",
                    null
                )
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            btnInsertPhoto.isEnabled = true
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCarDetails() {
        val plate = plateNumber.text.toString().trim()
        val carColor = color.text.toString().trim()

        if (plate.isEmpty() || carColor.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Save image to Firebase Storage
        val filePath = storageReference.child("car_images/${System.currentTimeMillis()}.jpg")
        filePath.putFile(selectedImageUri!!).addOnSuccessListener {
            // Get download URL
            filePath.downloadUrl.addOnSuccessListener { downloadUri ->
                // Save car details to Firestore
                val carData = hashMapOf(
                    "plate_number" to plate,
                    "color" to carColor,
                    "image_url" to downloadUri.toString(),
                    "timestamp" to System.currentTimeMillis()
                )

                // Save to the cars collection first
                firestore.collection("parkingSpots").add(carData)
                    .addOnSuccessListener { documentReference ->
                        // Save the parking spot ID
                        parkingSpotId = documentReference.id

                        // Update the filled status in Firestore
                        updateFilledStatus()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to save car details: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to get image URL: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFilledStatus() {
        if (parkingSpotId != null) {
            firestore.collection("parkingSpots").document(parkingSpotId!!)
                .update("filled", true)
                .addOnSuccessListener {
                    Toast.makeText(this, "Parking spot updated to filled!", Toast.LENGTH_SHORT)
                        .show()

                    // Set the result to indicate success and the new filled status
                    val resultIntent = Intent().apply {
                        putExtra("IS_FILLED", true) // Pass back the filled status
                        putExtra("PARKING_SPOT_ID", parkingSpotId) // Optional: pass the spot ID
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish() // Close the activity
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Failed to update parking spot filled status",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}

