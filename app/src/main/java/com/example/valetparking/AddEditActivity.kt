package com.example.valetparking

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
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
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var selectedImageUri: Uri? = null
    private var parkingSpotId: String? = null // To hold the parking spot ID

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
        btnSave = findViewById(R.id.btn_save)
        btnDelete = findViewById(R.id.btn_delete)

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

        btnSave.setOnClickListener {
            saveCarDetails()
        }

        // Delete button click listener
        btnDelete.setOnClickListener {
            val intent = Intent(this, ImageQrActivity::class.java)
            startActivity(intent)
        }


        // Retrieve spot data from intent
        parkingSpotId = intent.getStringExtra("PARKING_SPOT_ID")

        // If editing an existing spot, load the data from Firestore
        if (parkingSpotId != null) {
            loadExistingSpotData(parkingSpotId!!)
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

    private fun deleteCarFromFirebase(spotNumber: Int) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val carDoc = db.collection("users").document(userId)
                .collection("cars").document(spotNumber.toString())

            carDoc.delete().addOnSuccessListener {
                Toast.makeText(this, "Car removed!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to remove car.", Toast.LENGTH_SHORT).show()
            }
        }
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

    private fun loadExistingSpotData(spotId: String) {
        firestore.collection("parkingSpots").document(spotId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val plate = document.getString("plate_number") ?: ""
                    val carColor = document.getString("color") ?: ""
                    val imageUrl = document.getString("image_url")

                    // Pre-fill the form fields with retrieved data
                    plateNumber.setText(plate)
                    color.setText(carColor)

                    // Load the image if available
                    if (!imageUrl.isNullOrEmpty()) {
                        Picasso.get().load(imageUrl).into(imgView)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load parking spot data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveCarDetails() {
        val plate = plateNumber.text.toString().trim()
        val carColor = color.text.toString().trim()

        if (plate.isEmpty() || carColor.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Upload image to Firebase Storage
        val filePath = storageReference.child("car_images/${System.currentTimeMillis()}.jpg")
        filePath.putFile(selectedImageUri!!).addOnSuccessListener {
            filePath.downloadUrl.addOnSuccessListener { downloadUri ->
                val carData = hashMapOf(
                    "plate_number" to plate,
                    "color" to carColor,
                    "image_url" to downloadUri.toString(),
                    "timestamp" to System.currentTimeMillis(),
                    "userId" to userId // Save userId reference
                )

                firestore.collection("parkingSpots").add(carData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Car details saved!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to save car details: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

}

