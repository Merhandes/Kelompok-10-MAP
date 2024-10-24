package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ResetCompleteActivity : AppCompatActivity() {

    private lateinit var btnBackLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_complete)

        // Bind the button
        btnBackLogin = findViewById(R.id.btnBackLogin)

        // Set up button click listener
        btnBackLogin.setOnClickListener {
            // Redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close the ResetCompleteActivity
        }
    }
}
