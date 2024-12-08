package com.example.valetparking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.if570_lab_uts_merhandesgunawan_00000081070.UpdateProfileFragment

class UpdateProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)  // Ensure the correct layout

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UpdateProfileFragment())  // Fragment will be added to this container
                .commit()
        }
    }
}
