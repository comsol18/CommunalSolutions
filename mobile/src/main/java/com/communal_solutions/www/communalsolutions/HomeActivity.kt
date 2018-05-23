package com.communal_solutions.www.communalsolutions

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun getUserLoggedIn(view: View) {
        var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            var profile: TextView = userLoggedIn
            val uName = "User: " + user.displayName
            val uEmail = "\nEmail: " + user.email
            val uPhone = "\nPhone: " + user.phoneNumber
            profile.setText(uName + uEmail + uPhone)
        }
    }
}
