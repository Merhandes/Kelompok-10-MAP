package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.valetparking.databinding.ActivityVerificationBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.random.Random

class VerificationActivity : AppCompatActivity() {
    lateinit var binding: ActivityVerificationBinding
    lateinit var auth: FirebaseAuth
    private var email: String = ""
    private var pass: String = ""
    private var randomOtp: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        email = intent.getStringExtra("email").toString()
        pass = intent.getStringExtra("pass").toString()
        auth = FirebaseAuth.getInstance()

        generateRandomOtp()

        binding.btnVerify.setOnClickListener {
            val otpEntered = "${binding.otp1.text}${binding.otp2.text}${binding.otp3.text}${binding.otp4.text}"

            if (otpEntered.length != 4) {
                Toast.makeText(this@VerificationActivity, "Enter a 4-digit OTP", Toast.LENGTH_SHORT).show()
            } else if (otpEntered != randomOtp.toString()) {
                Toast.makeText(this@VerificationActivity, "Wrong OTP", Toast.LENGTH_SHORT).show()
            } else {
                createFirebaseUser(email, pass)
            }
        }
    }

    private fun generateRandomOtp() {
        randomOtp = Random.nextInt(1000, 9999)
        val sendMail = SendMail(
            "parkingvalet886@gmail.com",
            "wmpcwolhzgcgfrf",  // App-specific password for Gmail
            email,
            "Valet Parking App OTP",
            "Your OTP is $randomOtp"
        )
        sendMail.execute()
    }

    private fun createFirebaseUser(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this@VerificationActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this@VerificationActivity,
                    it.exception?.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
