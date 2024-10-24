package com.example.valetparking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.GridLayout

class ParkingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking)

        // Set up buttons for each parking slot
        val slot1: Button = findViewById(R.id.button_add_edit_1)
        val slot2: Button = findViewById(R.id.button_add_edit_2)
        val slot3: Button = findViewById(R.id.button_add_edit_3)
        val slot4: Button = findViewById(R.id.button_add_edit_4)
        val slot5: Button = findViewById(R.id.button_add_edit_5)
        val slot6: Button = findViewById(R.id.button_add_edit_6)
        val slot7: Button = findViewById(R.id.button_add_edit_7)
        val slot8: Button = findViewById(R.id.button_add_edit_8)
        val slot9: Button = findViewById(R.id.button_add_edit_9)
        val slot10: Button = findViewById(R.id.button_add_edit_10)


        slot1.setOnClickListener {
            openAddEditActivity()
        }
        slot2.setOnClickListener {
            openAddEditActivity()
        }
        slot3.setOnClickListener {
            openAddEditActivity()
        }
        slot4.setOnClickListener {
            openAddEditActivity()
        }
        slot5.setOnClickListener {
            openAddEditActivity()
        }
        slot6.setOnClickListener {
            openAddEditActivity()
        }
        slot7.setOnClickListener {
            openAddEditActivity()
        }
        slot8.setOnClickListener {
            openAddEditActivity()
        }
        slot9.setOnClickListener {
            openAddEditActivity()
        }
        slot10.setOnClickListener {
            openAddEditActivity()
        }


        // Repeat the above code for other slots
    }

    private fun openAddEditActivity() {
        val intent = Intent(this, AddEditActivity::class.java)
        startActivity(intent)
    }
}
