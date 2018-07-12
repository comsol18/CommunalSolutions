package com.communal_solutions.www.communalsolutions

import android.content.pm.PackageManager
import android.location.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.*
import com.communal_solutions.www.communalsolutions.HelperFiles.*
import com.google.firebase.database.*
import com.communal_solutions.www.communalsolutions.Managers.*
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    // Managers
    private var locationManager: LocationManager? = null
    private lateinit var sManager: SettingsManager
    private lateinit var dbManager: DatabaseManager

    // Views
    private var spinner: Spinner? = null
    private var toolbarTextView: TextView? = null

    // Listeners
    private var profileListener: ValueEventListener? = null

    private fun configureToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
    }

    private fun writeProfileData(profile: Profile, contactList: ContactList) {
        dbManager.getReference("users")!!.setValue(profile)
        dbManager.getReference("contacts")!!.setValue(contactList)
    }

    /*
    private fun getContacts(): ContactList {
        val contactList = ContactList()
        return contactList
    }

    private fun updateProfile() {
        val displayName = editDisplayName.text.toString()
        var phoneNum = editPhoneNum.text.toString()
        val email = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val username = editUsername.text.toString()

        // Validate Phone Number
        phoneNum = sManager.validateNumber(phoneNum)
        if (TextUtils.isEmpty(phoneNum)) editPhoneNum.setError("InvalidNumber")

        // initilize Profile object
        val profile = Profile(displayName, username, phoneNum, email, uid!!)
        val contactList = getContacts()

        // push data to database
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                sManager.writeProfileData(contactList)
                Toast.makeText(this@SettingsActivity, "Profile Updated", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Error", "onCancelled: Failed to read user!")
                Toast.makeText(this@SettingsActivity, "Profile Failed To Update", Toast.LENGTH_SHORT).show()
            }
        }

        dbPrivateReferences!!.userReference.child(uid!!).addListenerForSingleValueEvent(updateListener)
    }
    */

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val lat: Double = Math.round(location.latitude*1000.0)/1000.0
            val long: Double = Math.round(location.longitude*1000.0)/1000.0
            //gpsCoordinates.setText("(${lat}, ${long})")

            val userLocation = UserLocation(lat, long, dbManager.uuid.toString())
            dbManager.getReference("locations")!!.setValue(userLocation)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initiate the Settings Manager
        sManager = SettingsManager(intent)
        sManager.initEditTexts( editDisplayName, editUsername, editEmail, editPhoneNum )
        dbManager = DatabaseManager()

        // Configure the toolbar
        toolbarTextView = findViewById(R.id.toolbarTextView) as TextView
        spinner = findViewById(R.id.spinner) as Spinner
        configureToolbar()
        toolbarTextView!!.visibility = View.VISIBLE

        /* Set the Status spinner
        val stat = intent.getStringExtra("status")
        val statSpinner = spinner
        statSpinner.set
        */

        // set onclicklistener for save button
        saveSettings.setOnClickListener {
            sManager.initEditTexts(editEmail, editPhoneNum)
            sManager.updateProfile(this@SettingsActivity, editDisplayName, editPhoneNum, editUsername)
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

        contact1.setOnClickListener {
            val contactsIntent = Intent(this, ContactsActivity::class.java)
            startActivity(contactsIntent)
        }
    }

    @Synchronized override fun onStart() {
        super.onStart()

        //define a ValueEventListener
        val profileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // if dataSnapshot exists
                if (dataSnapshot.exists()) {
                    val profile = dataSnapshot.child(dbManager.uuid.toString()).getValue(Profile::class.java)
                    if (profile != null) {
                        sManager.setProfile(profile)
                        sManager.initEditTexts(editDisplayName, editUsername, editEmail, editPhoneNum)
                    } else {
                        sManager.initEditTexts(editEmail, editPhoneNum)
                        sManager.updateProfile(this@SettingsActivity, editDisplayName, editPhoneNum, editUsername)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@SettingsActivity, "Error: Failed to read user data", Toast.LENGTH_LONG).show()
            }
        }

        // add listener to reference
        dbManager.getReference("users")!!.addValueEventListener(profileListener)

        // set global listener to the listener defined
        this.profileListener = profileListener
    }

    override fun onStop() {
        super.onStop()
        //remove the listener
        if (profileListener != null) {
            dbManager.getReference("users")!!.removeEventListener(profileListener!!)
        }
    }
}

/*
    private fun validateNumber(number: String): String {
        var phoneNum = number
        if(phoneNum.length == 10) {
            for (c in phoneNum) {
                if (!c.isDigit()) {
                    editPhoneNum.setError("Invalid Number")
                    phoneNum = ""
                    return phoneNum
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
        return phoneNum
    }
    */
