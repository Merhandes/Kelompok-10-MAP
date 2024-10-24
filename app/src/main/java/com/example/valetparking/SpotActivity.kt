package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SpotActivity : AppCompatActivity() {

    private lateinit var dateTimeTextView: TextView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot)
        val btnBack: TextView = findViewById(R.id.btnBack)
        val btnEdit1: TextView = findViewById(R.id.etEdit1)

        // Inisialisasi TextView
        dateTimeTextView = findViewById(R.id.tv_date_time_edit)

        // Memulai pembaruan waktu secara real-time
        startRealTimeUpdate()

        btnBack.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        btnEdit1.setOnClickListener{
            val intent = Intent(this, AddEditActivity::class.java)
            startActivity(intent)
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
}