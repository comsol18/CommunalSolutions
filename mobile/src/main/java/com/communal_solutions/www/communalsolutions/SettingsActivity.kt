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

    // Database References
    private val dbValues = DBValues()
    private val dbReferences = DBReferences()

    // Managers
    private var locationManager: LocationManager? = null
    private lateinit var sManager: SettingsManager

    // Views
    private var spinner: Spinner? = null
    private var toolbarTextView: TextView? = null

    // Listeners
    private var profileListener: ValueEventListener? = null

    private fun setOnClickListeners(views: ArrayList<View>) {
        for (view in views) {
            when (view) {
                contact1, contact2, contact3, contact4, contact5, contact6 -> {
                    view.setOnClickListener {
                        val contactsIntent = Intent(this, ContactsActivity::class.java)
                        startActivity(contactsIntent)
                    }
                }
                saveSettings -> {
                    view.setOnClickListener {
                        sManager.initEditTexts(editEmail, editPhoneNum)
                        sManager.updateProfile(this@SettingsActivity, editDisplayName, editPhoneNum, editUsername)
                    }
                }
                else -> {}
            }
        }
    }

    private fun configureToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val lat: Double = Math.round(location.latitude*1000.0)/1000.0
            val long: Double = Math.round(location.longitude*1000.0)/1000.0
            val userLocation = UserLocation(lat, long, dbValues.uuid)
            dbReferences.locReference.setValue(userLocation)
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

        // Configure the toolbar
        toolbarTextView = findViewById(R.id.toolbarTextView) as TextView
        spinner = findViewById(R.id.spinner) as Spinner
        configureToolbar()
        toolbarTextView!!.visibility = View.VISIBLE

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

        setOnClickListeners(arrayListOf(contact1, contact2, contact3, contact4, contact5, contact6, saveSettings))
    }

    @Synchronized override fun onStart() {
        super.onStart()

        //define a ValueEventListener
        val profileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // if dataSnapshot exists
                if (dataSnapshot.exists()) {
                    val profile = dataSnapshot.getValue(Profile::class.java)
                    if (profile != null) {
                        sManager.profile = profile
                        sManager.initEditTexts(editDisplayName, editUsername, editEmail, editPhoneNum)
                    } else {
                        sManager.initEditTexts(editEmail, editPhoneNum)
                        sManager.updateProfile(this@SettingsActivity, editDisplayName, editPhoneNum, editUsername)
                        Toast.makeText(this@SettingsActivity, "Profile is Null", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@SettingsActivity, "Error: Failed to read user data", Toast.LENGTH_LONG).show()
            }
        }

        // add listener to reference
        dbReferences.userReference.addValueEventListener(profileListener)

        // set global listener to the listener defined
        this.profileListener = profileListener
    }

    override fun onStop() {
        super.onStop()
        //remove the listener
        if (profileListener != null) {
            dbReferences.userReference.removeEventListener(profileListener!!)
        }
    }
}

