package com.example.valetparking

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddVehicleActivity : AppCompatActivity() {

    private lateinit var editTextPlateNumber: EditText
    private lateinit var imageViewVehicle: ImageView
    private var photoUri: Uri? = null

    private val REQUEST_PERMISSION_CAMERA = 1001
    private val REQUEST_PERMISSION_STORAGE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicle)

        // Cek dan minta izin
        checkPermissions()

        // Inisialisasi elemen UI
        editTextPlateNumber = findViewById(R.id.edit_text_plate_number)
        imageViewVehicle = findViewById(R.id.image_view_vehicle)

        val buttonTakePhoto: Button = findViewById(R.id.button_take_photo)
        buttonTakePhoto.setOnClickListener { dispatchTakePictureIntent() }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Izin kamera diberikan
                } else {
                    // Tampilkan pesan bahwa izin dibutuhkan
                }
            }
            REQUEST_PERMISSION_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Izin penyimpanan diberikan
                } else {
                    // Tampilkan pesan bahwa izin dibutuhkan
                }
            }
        }
    }
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Pastikan ada aplikasi kamera yang dapat menangani intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                // Membuat file untuk menyimpan gambar
                val photoFile = createImageFile()
                photoUri = Uri.fromFile(photoFile) // Simpan URI foto
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri) // Kirim URI ke intent
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE) // Mulai activity kamera
            } catch (ex: IOException) {
                // Tangani error saat membuat file
                ex.printStackTrace()
            }
        } else {
            // Tampilkan pesan bahwa kamera tidak tersedia
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Buat nama gambar dan file
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val timeStamp_ = ""
        return File.createTempFile(
            "JPEG_$timeStamp_", /* prefix */
            ".jpg",             /* suffix */
            storageDir          /* directory */
        ).apply {
            // Simpan path ke file
            photoUri = Uri.fromFile(this)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Mengatur gambar ke ImageView
            imageViewVehicle.setImageURI(photoUri)
            imageViewVehicle.visibility = ImageView.VISIBLE
        }
    }

    private fun saveVehicle() {
        val plateNumber = editTextPlateNumber.text.toString()
        // Simpan informasi kendaraan (nomor plat dan foto) sesuai kebutuhan
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}
