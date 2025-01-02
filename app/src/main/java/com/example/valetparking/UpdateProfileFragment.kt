package com.example.if570_lab_uts_merhandesgunawan_00000081070

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.example.valetparking.R

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdateProfileFragment : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_profile, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        nameEditText = view.findViewById(R.id.nameEditText)
        saveButton = view.findViewById(R.id.saveButton)

        loadUserProfile()

        saveButton.setOnClickListener {
            saveProfile()
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
                        nameEditText.setText(document.getString("name"))
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to load profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveProfile() {
        val name = nameEditText.text.toString().trim()

        if (name.isEmpty()) {
            nameEditText.error = "Name is required"
            return
        }

        val user = auth.currentUser
        if (user != null) {
            // Menggunakan update untuk hanya mengubah field "name"
            val updates = mapOf("name" to name)

            db.collection("users").document(user.uid)
                .update(updates)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()

                    activity?.setResult(AppCompatActivity.RESULT_OK)
                    activity?.finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
