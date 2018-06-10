package com.communal_solutions.www.communalsolutions

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase

class SettingsActivity : AppCompatActivity() {

    private var dbHandler: DatabaseHandler = DatabaseHandler()

    private fun initValues() {
        val name = findViewById<EditText>(R.id.editDisplayName)
        val email = findViewById<EditText>(R.id.editEmail)
        val num = findViewById<EditText>(R.id.editPhoneNum)
        val uName = findViewById<EditText>(R.id.editUsername)

        name.setText(dbHandler.getDisplayName())
        email.setText(dbHandler.getEmail())
        num.setText(dbHandler.getPhoneNumber())
        uName.setText(dbHandler.getUserName())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initValues()

        //val test: EditText = findViewById(R.id.testField)
        //test.setOnEditorActionListener( TextView.OnEditorActionListener())
    }
}
