package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class VerificationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var phoneNumber: String = ""
    private var verificationCode: String = ""
    private var resendingToken: PhoneAuthProvider.ForceResendingToken? = null

    private lateinit var otpInput: EditText
    private lateinit var btnVerify: Button
    private lateinit var resendOtpTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        auth = FirebaseAuth.getInstance()

        phoneNumber = intent.getStringExtra("phone").toString()

        otpInput = findViewById(R.id.otpInput)
        btnVerify = findViewById(R.id.btnVerify)
        resendOtpTextView = findViewById(R.id.resendOtpTextView)

        sendOtp(phoneNumber)

        btnVerify.setOnClickListener {
            val enteredOtp = otpInput.text.toString()
            val credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp)
            signIn(credential)
        }

        resendOtpTextView.setOnClickListener {
            sendOtp(phoneNumber, true)
        }

        setupOTPInputs(otpInput)
    }

    private fun sendOtp(phoneNumber: String, isResend: Boolean = false) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signIn(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(applicationContext, "OTP verification failed", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(code: String, token: PhoneAuthProvider.ForceResendingToken) {
                    verificationCode = code
                    resendingToken = token
                    Toast.makeText(applicationContext, "OTP sent successfully", Toast.LENGTH_SHORT).show()
                }
            })

        if (isResend && resendingToken != null) {
            options.setForceResendingToken(resendingToken!!)
        }

        PhoneAuthProvider.verifyPhoneNumber(options.build())
    }

    private fun signIn(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                startActivity(Intent(this, LoginActivity::class.java)) // Replace with your next activity
                finish()
            } else {
                Toast.makeText(this, "OTP verification failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupOTPInputs(vararg inputs: EditText) {
        for (i in inputs.indices) {
            inputs[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < inputs.size - 1) {
                        inputs[i + 1].requestFocus()
                    } else if (s?.isEmpty() == true && i > 0) {
                        inputs[i - 1].requestFocus()
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }
}