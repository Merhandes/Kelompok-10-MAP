package com.example.valetparking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddEditActivity : AppCompatActivity() {

    private lateinit var plateNumber: EditText
    private lateinit var color: EditText
    private lateinit var insertPhotoButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        plateNumber = findViewById(R.id.et_plate_number)
        color = findViewById(R.id.et_color)
        insertPhotoButton = findViewById(R.id.btn_insert_photo)

        insertPhotoButton.setOnClickListener {
            openCamera()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            // Handle the captured image here, save it or display it
        }
    }
}
