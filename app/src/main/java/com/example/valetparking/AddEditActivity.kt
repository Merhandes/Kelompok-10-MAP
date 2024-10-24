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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.Manifest
import android.util.Log


class AddEditActivity : AppCompatActivity() {

    private lateinit var plateNumber: EditText
    private lateinit var color: EditText
    private lateinit var dateTimeTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var btnInsertPhoto: Button
    private lateinit var imgView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        plateNumber = findViewById(R.id.et_plate_number)
        color = findViewById(R.id.et_color)
        btnInsertPhoto = findViewById(R.id.btn_insert_photo)
        imgView = findViewById(R.id.img_view)

        // Inisialisasi TextView
        dateTimeTextView = findViewById(R.id.tv_date_time_edit)

        // Memulai pembaruan waktu secara real-time
        startRealTimeUpdate()


        btnInsertPhoto.isEnabled = false
        // Periksa izin kamera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            btnInsertPhoto.isEnabled = true
        }

        btnInsertPhoto.setOnClickListener {
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i, 101)
        }
    }



    private fun startRealTimeUpdate() {
        val runnable = object : Runnable {
            override fun run() {
                // Format waktu sesuai kebutuhan
                val currentTime = Calendar.getInstance().time
                val dateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.getDefault())
                val formattedTime = dateFormat.format(currentTime)

                // Set waktu ke TextView
                dateTimeTextView.text = formattedTime

                // Memperbarui TextView setiap 1 detik
                handler.postDelayed(this, 1000)
            }
        }

        // Memulai pembaruan pertama kali
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hentikan handler ketika activity dihancurkan untuk mencegah memory leaks
        handler.removeCallbacksAndMessages(null)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            val photo: Bitmap? = data?.getParcelableExtra("data")
            imgView.setImageBitmap(photo)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            btnInsertPhoto.isEnabled = true
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}
