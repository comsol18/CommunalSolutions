package com.communal_solutions.www.communalsolutions

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {

    private var db = FirebaseDatabase.getInstance()
    private var cUser = FirebaseAuth.getInstance().currentUser!!
    private var profileSchema = ProfileSchema("","----------","fine")

    fun updateProfile(view: View) {
        val users = db.getReference("users")

        val editStatus = findViewById<EditText>(R.id.editStatus)
        val editName = findViewById<EditText>(R.id.editDisplayName)
        val editNum = findViewById<EditText>(R.id.editPhoneNum)

        profileSchema.profileName = editName.text.toString()
        profileSchema.phoneNum = editNum.text.toString()
        profileSchema.status = editStatus.text.toString()

        users.child(cUser.uid.hashCode().toString()).setValue(profileSchema)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        
    }
}
