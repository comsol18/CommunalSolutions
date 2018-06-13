package com.communal_solutions.www.communalsolutions

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_settings.*

class HomeActivity : AppCompatActivity() {

    private val db = FirebaseDatabase.getInstance()
    private val cUser = FirebaseAuth.getInstance().currentUser
    private val userReference = db.getReference("users")
    private val uid = cUser!!.uid.hashCode().toString()
    private var profileListener: ValueEventListener? = null
    private var passProfile: Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()

        val profileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // if dataSnapshot exists
                if (dataSnapshot.exists()) {
                    val profile = dataSnapshot.child(uid).getValue(Profile::class.java)
                    passProfile = profile
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Error: Failed to read user data", Toast.LENGTH_LONG).show()
            }
        }

        // add listener to reference
        userReference.addValueEventListener(profileListener)
        this.profileListener = profileListener
    }

    fun logout(view: View) {
        userReference.removeEventListener(profileListener!!)
        FirebaseAuth.getInstance().signOut()
        super.finish()
    }

    fun settings(view: View) {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        if (passProfile != null) {
            settingsIntent.putExtra("display_name", passProfile!!.profile_name)
            settingsIntent.putExtra("email", passProfile!!.email_address)
            settingsIntent.putExtra("user_name", passProfile!!.user_name)
            settingsIntent.putExtra("phone_number", passProfile!!.cell_number)
            settingsIntent.putExtra("status", passProfile!!.status)
        } else {
            settingsIntent.putExtra("display_name", "")
            settingsIntent.putExtra("email", cUser!!.email)
            settingsIntent.putExtra("user_name", cUser.email!!.substringBefore('@', ""))
            settingsIntent.putExtra("phone_number", "")
            settingsIntent.putExtra("status", "")
        }
        startActivity(settingsIntent)
    }
}
