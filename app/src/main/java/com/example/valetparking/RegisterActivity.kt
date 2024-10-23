package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.valetparking.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Setting up window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handling the "Next" button in the SignUp process
        binding.btnRegister.setOnClickListener {
            validateAndProceedToOtp()
        }

        // Login button leading to LoginActivity
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateAndProceedToOtp() {
        val email = binding.textEmail.editText?.text.toString()
        val password = binding.textPassword.editText?.text.toString()
        val confirmPassword = binding.textConfirmationPassword.editText?.text.toString()
        val phoneNumber = binding.textPhoneNumber.editText?.text.toString()

        // Validation checks
        when {
            email.isEmpty() -> binding.textEmail.error = "Enter Email"
            password.isEmpty() -> binding.textPassword.error = "Enter Password"
            confirmPassword.isEmpty() -> binding.textConfirmationPassword.error = "Enter Password Again"
            password != confirmPassword -> binding.textConfirmationPassword.error = "Passwords must match"
            phoneNumber.isEmpty() -> binding.textPhoneNumber.error = "Enter Phone Number"
            else -> {
                // Registrasi pengguna menggunakan Firebase Authentication
                registerUser(email, password, phoneNumber)
            }
        }
    }

    // Fungsi untuk registrasi pengguna menggunakan Firebase Authentication
    private fun registerUser(email: String, password: String, phoneNumber: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Jika pendaftaran berhasil, arahkan ke VerificationActivity
                    val intent = Intent(this, VerificationActivity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("phone", phoneNumber)
                    startActivity(intent)
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                } else {
                    // Jika pendaftaran gagal, tampilkan pesan error
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}

