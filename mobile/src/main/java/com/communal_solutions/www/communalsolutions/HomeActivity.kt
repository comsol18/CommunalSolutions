package com.communal_solutions.www.communalsolutions

import android.content.Context
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
import kotlin.math.roundToInt
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.content.Context.TELEPHONY_SERVICE
import android.telephony.TelephonyManager
import android.Manifest
import android.util.Log


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
        getPermissions()

        val profileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // if dataSnapshot exists
                if (dataSnapshot.exists()) {
                    val profile = dataSnapshot.child("private").child(uid).getValue(Profile::class.java)
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

    override fun onStart() {
        super.onStart()
        val clicker: View.OnClickListener = View.OnClickListener {
            when (it) {
                logOut -> logout()
                settings -> loadSettings()
            }
        }
        logOut.setOnClickListener(clicker)
        settings.setOnClickListener(clicker)
    }

    private fun getPermissions() {
        val MY_PERMISSIONS_REQUEST= 9002
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@HomeActivity,
                    arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS),
                    MY_PERMISSIONS_REQUEST)
        } /*else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@HomeActivity,
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    MY_PERMISSIONS_REQUEST)
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@HomeActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST)
        }*/
    }

    private fun getUserNumber(): String {
        val phoneMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            var number: String = phoneMgr.line1Number
            number = if (number.length == 11) number.substring(1)  else number
            return number.substring(0, 3) + "-" + number.substring(3, 6) + "-" + number.substring(6)
        } catch (e: SecurityException) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                val MY_PERMISSIONS_REQUEST = 9002
                ActivityCompat.requestPermissions(this@HomeActivity,
                        arrayOf(Manifest.permission.READ_PHONE_STATE),
                        MY_PERMISSIONS_REQUEST)
            }
            try {
                var number: String = phoneMgr.line1Number
                number = if (number.length == 11) number.substring(1)  else number
                return number.substring(0, 3) + "-" + number.substring(3, 6) + "-" + number.substring(6)
            } catch (ex: SecurityException) {
                Toast.makeText(this, "Phone Permissions not granted", Toast.LENGTH_SHORT).show()
                Log.e("getUserNumber", ex.toString())
                return ""
            }
        }
    }

    @Synchronized private fun loadSettings() {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        val hashCode: Int = cUser!!.uid.hashCode()
        if (passProfile != null) {
            settingsIntent.putExtra("display_name", passProfile!!.profile_name)
            settingsIntent.putExtra("email", passProfile!!.email_address)
            settingsIntent.putExtra("user_name", passProfile!!.user_name)
            settingsIntent.putExtra("phone_number", getUserNumber())
            settingsIntent.putExtra("status", passProfile!!.status)
        } else {
            settingsIntent.putExtra("display_name", "random_profile_name${Math.abs(hashCode.hashCode())}")
            settingsIntent.putExtra("email", cUser.email)
            settingsIntent.putExtra("user_name", cUser.email!!.substringBefore('@', ""))
            settingsIntent.putExtra("phone_number", getUserNumber())
            settingsIntent.putExtra("status", "")
        }
        startActivity(settingsIntent)
    }

    fun logout() {
        userReference.removeEventListener(profileListener!!)
        FirebaseAuth.getInstance().signOut()
        super.finish()
    }

}
