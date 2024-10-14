package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.valetparking.databinding.ActivityRegisterBinding
import papaya.`in`.sendmail.SendMail // Ensure this package is correct and the SendMail class is implemented correctly
import com.google.firebase.auth.FirebaseAuth
import kotlin.random.Random

class VerificationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var email: String = ""
    private var pass: String = ""
    private var randomOtp: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_verification)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Retrieve email and password from Intent
        email = intent.getStringExtra("email").toString()
        pass = intent.getStringExtra("pass").toString()

        // Generate and send OTP
        generateAndSendOtp()

        // Set up UI components
        val btnVerify: Button = findViewById(R.id.btnVerify)
        val inputCode1: EditText = findViewById(R.id.otp1)
        val inputCode2: EditText = findViewById(R.id.otp2)
        val inputCode3: EditText = findViewById(R.id.otp3)
        val inputCode4: EditText = findViewById(R.id.otp4)

        // Set click listener for verification button
        btnVerify.setOnClickListener {
            verifyOtp(inputCode1, inputCode2, inputCode3, inputCode4)
        }

        // Set up OTP input fields
        setupOTPInputs(inputCode1, inputCode2, inputCode3, inputCode4)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun generateAndSendOtp() {
        randomOtp = Random.nextInt(100000, 999999) // Inclusive of 100000 and exclusive of 999999
        val mail = SendMail("parkingvalet886@gmail.com", "wmpcwolhzgcgfrf", email, "Valet Parking Register OTP", "Your OTP is -> $randomOtp")
        mail.execute() // Ensure the SendMail class handles exceptions and sends the email correctly
    }

    private fun verifyOtp(inputCode1: EditText, inputCode2: EditText, inputCode3: EditText, inputCode4: EditText) {
        val otp = "${inputCode1.text}${inputCode2.text}${inputCode3.text}${inputCode4.text}"

        when {
            otp.isEmpty() -> {
                Toast.makeText(this, "Enter complete OTP", Toast.LENGTH_SHORT).show()
            }
            otp.length < 4 -> {
                Toast.makeText(this, "Enter complete OTP", Toast.LENGTH_SHORT).show()
            }
            otp != randomOtp.toString() -> {
                Toast.makeText(this, "Wrong OTP", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // Authenticate user with Firebase
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, LoginActivity::class.java)) // Redirect to LoginActivity
                        finish() // Optionally, close the current activity
                    } else {
                        Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupOTPInputs(vararg inputs: EditText) {
        for (i in inputs.indices) {
            inputs[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < inputs.size - 1) {
                        inputs[i + 1].requestFocus() // Move focus to the next input
                    } else if (s?.isEmpty() == true && i > 0) {
                        inputs[i - 1].requestFocus() // Move focus to the previous input if current is empty
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }
}
