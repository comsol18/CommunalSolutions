package com.communal_solutions.www.communalsolutions

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
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
import kotlin.math.roundToInt

data class Profile(
    // profile info
    val profile_name: String = "",
    val user_name: String = "",
    val cell_number: String = "",
    val email_address: String = "",
    val status: String = "",
    var uuid: String = ""
)

data class UserLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var uuid: String = ""
)

data class Contact(
    val cName: String = "",
    val cPhoneNum: String = ""
)

data class ContactList(
    var contact1: Contact = Contact(),
    var contact2: Contact = Contact(),
    var contact3: Contact = Contact(),
    var contact4: Contact = Contact(),
    var contact5: Contact = Contact(),
    var contact6: Contact = Contact()
)

class SettingsActivity : AppCompatActivity() {

    // database, current user, and db references
    private var db: FirebaseDatabase? = null
    private var cUser: FirebaseUser? = null
    private var dbReference: DatabaseReference? = null
    private var userReference: DatabaseReference? = null
    private var contactReference: DatabaseReference? = null
    private var profileListener: ValueEventListener? = null
    private var uid: String? = null

    private var locationManager: LocationManager? = null

    private fun writeProfileData(profile: Profile, contactList: ContactList) {
        userReference!!.child(uid!!).setValue(profile)
        contactReference!!.child(uid!!).setValue(contactList)
    }

    private fun getContacts(): ContactList {
        val contactList = ContactList()
        return contactList
    }

    private fun updateProfile() {
        editPhoneNum.setText(intent.getStringExtra("phone_number"))
        editEmail.setText(intent.getStringExtra("email"))
        val displayName = editDisplayName.text.toString()
        var phoneNum = editPhoneNum.text.toString()
        val status = editStatus.text.toString()
        val email = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val username = editUsername.text.toString()

        if(phoneNum.length == 10) {
            for (c in phoneNum) {
                if (!c.isDigit()) {
                    editPhoneNum.setError("Invalid Number")
                    phoneNum = ""
                    break
                }
            }
            phoneNum = phoneNum.substring(0, 3) + "-" + phoneNum.substring(3, 6) + "-" + phoneNum.substring(6, 10)
        } else if (phoneNum.length == 12) {
            loop@ for (i in 0..11) {
                when (i) {
                    3, 7 -> {
                        if (phoneNum[i] != '-') {
                            editPhoneNum.setError("Invalid Number")
                            phoneNum = ""
                            break@loop
                        }
                    }
                    else -> {
                        if (!phoneNum[i].isDigit()) {
                            editPhoneNum.setError("Invalid Number")
                            phoneNum = ""
                            break@loop
                        }
                    }
                }
            }
        } else {
            editPhoneNum.setError("Invalid Number")
            phoneNum = ""
        }

        // initilize Profile object
        val profile = Profile(displayName, username, phoneNum, email, status, uid!!)
        val contactList = getContacts()

        // push data to database
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                writeProfileData(profile, contactList)
                Toast.makeText(this@SettingsActivity, "Profile Updated", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Error", "onCancelled: Failed to read user!")
                Toast.makeText(this@SettingsActivity, "Profile Failed To Update", Toast.LENGTH_SHORT).show()
            }
        }

        dbReference!!.child("users").child(uid!!).addListenerForSingleValueEvent(updateListener)
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val lat: Double = Math.round(location.latitude*1000.0)/1000.0
            val long: Double = Math.round(location.longitude*1000.0)/1000.0
            //gpsCoordinates.setText("(${lat}, ${long})")

            val userLocation = UserLocation(lat, long, uid!!)
            val locReference = dbReference!!.child("private").child("locations")
            locReference.child(uid!!).setValue(userLocation)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // define database, current user, and db references
        db = FirebaseDatabase.getInstance()
        cUser = FirebaseAuth.getInstance().currentUser
        dbReference = db!!.reference
        userReference = dbReference!!.child("private").child("users")
        contactReference = dbReference!!.child("private").child("contacts")
        uid = cUser!!.uid.hashCode().toString()

        editDisplayName.setText(intent.getStringExtra("display_name"))
        editUsername.setText(intent.getStringExtra("user_name"))
        editEmail.setText(intent.getStringExtra("email"))
        editPhoneNum.setText(intent.getStringExtra("phone_number"))
        editStatus.setText(intent.getStringExtra("status"))

        // set onclicklistener for save button
        saveSettings.setOnClickListener {
            updateProfile()
        }

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        val MY_PERMISSIONS_REQUEST = 9002

        try {
            // Request location updates
            if (ContextCompat.checkSelfPermission(this@SettingsActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this@SettingsActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST)
            } else {
                // Permission has already been granted
                locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
            }
        } catch(ex: SecurityException) {
            Log.e("Exception", ex.toString())
            Log.e("myTag", "Security Exception, no location available")
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

        // add listener to reference
        userReference!!.addValueEventListener(profileListener)

        // set global listener to the listener defined
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
