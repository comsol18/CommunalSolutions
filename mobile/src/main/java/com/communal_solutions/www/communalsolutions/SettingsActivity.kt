package com.communal_solutions.www.communalsolutions

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.JsonToken
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_settings.*

data class Profile(
    // profile info
    val profile_name: String = "",
    val user_name: String = "",
    val cell_number: String = "",
    val email_address: String = "",
    val status: String = "",
    var uuid: String = ""
)

class SettingsActivity : AppCompatActivity() {

    // database, current user, and db references
    private var db: FirebaseDatabase? = null
    private var cUser: FirebaseUser? = null
    private var dbReference: DatabaseReference? = null
    private var userReference: DatabaseReference? = null
    private var profileListener: ValueEventListener? = null
    private var uid: String? = null

    private fun writeProfileData(profile: Profile) {
        val uid = cUser!!.uid.hashCode().toString()
        userReference!!.child(uid).setValue(profile)
    }

    private fun updateProfile() {
        val displayName = editDisplayName.text.toString()
        val phoneNum = editPhoneNum.text.toString()
        val status = editStatus.text.toString()
        val email = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val username = email.substringBefore('@', "")

        // initilize Profile object
        val profile = Profile(displayName, username, phoneNum, email, status, uid!!)

        // push data to database
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userData = dataSnapshot.getValue(Profile::class.java)
                /*if (userData == null) {
                    Log.e("Error", "onDataChange: User data is null!")
                    Toast.makeText(this@SettingsActivity, "onDataChange: User data is null!", Toast.LENGTH_SHORT).show()
                    return
                }*/
                writeProfileData(profile)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Error", "onCancelled: Failed to read user!")
            }
        }

        dbReference!!.child("users").child(uid!!).addListenerForSingleValueEvent(updateListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // define database, current user, and db references
        db = FirebaseDatabase.getInstance()
        cUser = FirebaseAuth.getInstance().currentUser
        dbReference = db!!.reference
        userReference = db!!.getReference("users")
        uid = cUser!!.uid.hashCode().toString()

        // set onclicklistener for save button
        saveSettings.setOnClickListener {
            updateProfile()
        }
    }

    override fun onStart() {
        super.onStart()

        //define a ValueEventListener
        val profileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // if dataSnapshot exists
                if (dataSnapshot.exists()) {
                    val profile = dataSnapshot.child(uid!!).getValue(Profile::class.java)
                    if (profile != null) {
                        editDisplayName.setText(profile.profile_name)
                        editUsername.setText(profile.user_name)
                        editPhoneNum.setText(profile.cell_number)
                        editEmail.setText(profile.email_address)
                        editStatus.setText(profile.status)
                    } else {
                        updateProfile()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@SettingsActivity, "Error: Failed to read user data", Toast.LENGTH_LONG).show()
            }
        }
        //add listener to reference
        userReference!!.addValueEventListener(profileListener)
        //set global listener to the listener defined
        this.profileListener = profileListener
    }

    override fun onStop() {
        super.onStop()
        //remove the listener
        if (profileListener != null) {
            userReference!!.removeEventListener(profileListener!!)
        }
    }
}
