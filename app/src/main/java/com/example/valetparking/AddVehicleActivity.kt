package com.example.valetparking

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddVehicleActivity : AppCompatActivity() {

    private lateinit var editTextPlateNumber: EditText
    private lateinit var imageViewVehicle: ImageView
    private lateinit var buttonTakePhoto: Button
    private lateinit var buttonSaveVehicle: Button
    private lateinit var photoUri: Uri


    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicle)

        // Inisialisasi elemen UI
        editTextPlateNumber = findViewById(R.id.edit_text_plate_number)
        imageViewVehicle = findViewById(R.id.image_view_vehicle)
        buttonTakePhoto = findViewById(R.id.btn_camera)
        buttonSaveVehicle = findViewById(R.id.button_save_vehicle)

        // Inisialisasi ActivityResultLauncher untuk mengambil gambar
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                // Pastikan photoUri sudah diinisialisasi
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(photoUri))
                imageViewVehicle.setImageBitmap(bitmap)
            } else {
                Toast.makeText(this, "Gagal mengambil foto", Toast.LENGTH_SHORT).show()
            }
        }

        // Set listener untuk tombol mengambil foto
        buttonTakePhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }

        // Set listener untuk tombol menyimpan kendaraan
        buttonSaveVehicle.setOnClickListener {
            saveVehicle()
        }

        // Periksa dan minta izin
        checkPermissions()
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Tidak perlu izin untuk scoped storage di Android 10+
        }
    }

    private fun checkPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (!hasCameraPermission()) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !hasStoragePermission()) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_PERMISSIONS)
        }
    }

    private fun dispatchTakePictureIntent() {
        // Buat File tempat foto akan disimpan
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            // Tangani error saat membuat File
            ex.printStackTrace()
            null
        }

        // Lanjutkan hanya jika File berhasil dibuat
        photoFile?.also {
            photoUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                it
            )
            takePictureLauncher.launch(photoUri)
        } ?: run {
            // Tampilkan pesan jika File gagal dibuat
            Toast.makeText(this, "Tidak dapat membuat file gambar", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Buat nama file gambar dengan timestamp
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg",              /* suffix */
            storageDir           /* directory */
        )
    }

    private fun saveVehicle() {
        val plateNumber = editTextPlateNumber.text.toString().trim()
        if (plateNumber.isEmpty()) {
            Toast.makeText(this, "Nomor plat tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        if (photoUri == null) {
            Toast.makeText(this, "Silakan ambil foto kendaraan", Toast.LENGTH_SHORT).show()
            return
        }

        // Persiapkan data untuk dikirim kembali ke MainActivity
        val resultIntent = Intent().apply {
            putExtra("plate_number", plateNumber)
            putExtra("photo_uri", photoUri)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    var allGranted = true
                    for (result in grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            allGranted = false
                            break
                        }
                    }
                    if (!allGranted) {
                        Toast.makeText(this, "Izin diperlukan untuk mengambil foto", Toast.LENGTH_SHORT).show()
                        // Opsional: Nonaktifkan fungsi kamera
                        buttonTakePhoto.isEnabled = false
                        buttonSaveVehicle.isEnabled = false
                    }
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    companion object {
        private const val REQUEST_PERMISSIONS = 1001
    }
}
