package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SpotActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot)
        val btnBack: TextView = findViewById(R.id.btnBack)
        val btnEdit1: TextView = findViewById(R.id.etEdit1)

        btnBack.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        btnEdit1.setOnClickListener{
            val intent = Intent(this, AddEditActivity::class.java)
            startActivity(intent)
        }
    }
}