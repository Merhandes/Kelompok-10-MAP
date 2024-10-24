package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ForgotActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnSubmit: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Bind views
        etEmail = findViewById(R.id.etEmail)
        btnSubmit = findViewById(R.id.btnSubmit)
        val buttonBack: ImageButton = findViewById(R.id.buttonBack) // Bind ImageButton
        // Bind views
        val textInputLayout: TextInputLayout = findViewById(R.id.tilEmail) // Jika diperlukan
        etEmail = findViewById(R.id.etEmail)


        // Set up back button click listener
        buttonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Set up button submit click listener
        btnSubmit.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            } else {
                // Send password reset email
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Email sent successfully!", Toast.LENGTH_SHORT)
                                .show()
                            // Berpindah ke ForgotVerificationActivity
                            val intent = Intent(this, ForgotVerificationActivity::class.java)
                            intent.putExtra(
                                "email",
                                email
                            ) // Mengirim email ke activity selanjutnya jika diperlukan
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to send email. Try again!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

    }

}
