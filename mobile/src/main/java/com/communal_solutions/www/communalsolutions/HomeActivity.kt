package com.communal_solutions.www.communalsolutions

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun getUserLoggedIn(view: View) {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val profile: TextView = userLoggedIn
        val userText: String
        if (user != null) {
            userText = "User: " + user.displayName +
                "\nEmail: " + user.email +
                "\nPhone: " + user.phoneNumber
        } else {
            userText = "No User Logged In"
            Toast.makeText(this, "No User Logged In", Toast.LENGTH_SHORT).show()
        }
        profile.setText(userText)
    }

    fun logout(view: View) {
        FirebaseAuth.getInstance().signOut()
        val signOutIntent = Intent(this, MainActivity::class.java)
        startActivity(signOutIntent)
    }
}