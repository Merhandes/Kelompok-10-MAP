package com.example.valetparking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var buttonAddVehicle: Button
    private lateinit var addVehicleLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonAddVehicle = findViewById(R.id.button_add_vehicle)

        // Inisialisasi ActivityResultLauncher
        addVehicleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                data?.let {
                    val plateNumber = it.getStringExtra("plate_number")
                    val photoUri = it.getParcelableExtra<Uri>("photo_uri")
                    // Lakukan sesuatu dengan data yang diterima, misalnya menyimpannya ke database atau SharedPreferences
                    Toast.makeText(this, "Kendaraan ditambahkan: $plateNumber", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonAddVehicle.setOnClickListener {
            val intent = Intent(this, AddVehicleActivity::class.java)
            addVehicleLauncher.launch(intent)
        }
    }
}
