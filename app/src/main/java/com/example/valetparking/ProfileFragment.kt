package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var editProfileButton: Button
    private lateinit var logoutButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        const val REQUEST_CODE_UPDATE_PROFILE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        nameTextView = view.findViewById(R.id.nameTextView)
        editProfileButton = view.findViewById(R.id.EditProfile)
        logoutButton = view.findViewById(R.id.Logout)

        loadUserProfile()

        editProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), UpdateProfileActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_UPDATE_PROFILE)
        }


        logoutButton.setOnClickListener {
            auth.signOut()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear activity stack
            startActivity(intent)
        }

        return view
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            val docRef = db.collection("users").document(user.uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: "No Name Available"

                        nameTextView.text = name
                    } else {
                        nameTextView.text = "No Name Available"
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to load user data: ${e.message}", Toast.LENGTH_SHORT).show()
                    nameTextView.text = "No Name Available"
                }
        } else {
            Toast.makeText(requireContext(), "User is not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle result when coming back from UpdateProfileActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_UPDATE_PROFILE && resultCode == AppCompatActivity.RESULT_OK) {
            loadUserProfile()
        }
    }
}
