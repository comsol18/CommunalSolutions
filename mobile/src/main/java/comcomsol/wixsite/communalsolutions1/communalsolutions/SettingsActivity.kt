package comcomsol.wixsite.communalsolutions1.communalsolutions

import android.content.pm.PackageManager
import android.location.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.Context
import android.content.Context.*
import android.content.Intent
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.telephony.TelephonyManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.*
import com.google.firebase.database.*
import comcomsol.wixsite.communalsolutions1.communalsolutions.Adapters.ContactsAdapter
import comcomsol.wixsite.communalsolutions1.communalsolutions.Managers.*
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    private val TAG = "SettingsActivity"

    // Database References
//    private val dbValues = DBValues()
    private val dbReferences = DBReferences()

    // Managers
//    private var locationManager: LocationManager? = null
    private lateinit var sManager: SettingsManager

    // Views
    private var spinner: Spinner? = null
    private var toolbarTextView: TextView? = null

    // Listeners
    private var profileListener: ValueEventListener? = null
    private var contactsListener: ValueEventListener? = null

    private fun getUserNumber(): String {
        val phoneMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            var number: String = phoneMgr.line1Number
            number = if (number.length == 11) number.substring(1)  else number
            return number.substring(0, 3) + "-" + number.substring(3, 6) + "-" + number.substring(6)
        } catch (e: SecurityException) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                val MY_PERMISSIONS_REQUEST = 9002
                ActivityCompat.requestPermissions(this@SettingsActivity,
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

    private fun setOnClickListeners(views: ArrayList<View>) {
        for (view in views) {
            when (view) {
                contact1, contact2, contact3, contact4, contact5, contact6 -> {
                    view.setOnClickListener {
                        val contactsIntent = Intent(this, ContactsActivity::class.java)
                        val requestCode = when (view) {
                            contact1 -> 1
                            contact2 -> 2
                            contact3 -> 3
                            contact4 -> 4
                            contact5 -> 5
                            else -> 6
                        }
                        contactsIntent.putExtra("RequestCode", requestCode.toString())
                        startActivityForResult(contactsIntent, requestCode)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val returnContact = data!!.getParcelableExtra<Contact>("ContactSelected")
        when (requestCode) {
            1 -> sManager.contactList.contact1 = returnContact
            2 -> sManager.contactList.contact2 = returnContact
            3 -> sManager.contactList.contact3 = returnContact
            4 -> sManager.contactList.contact4 = returnContact
            5 -> sManager.contactList.contact5 = returnContact
            6 -> sManager.contactList.contact6 = returnContact
        }
        sManager.initEmergencyContacts(arrayListOf(contact1, contact2, contact3, contact4, contact5, contact6))
        sManager.updateContacts(this)
    }

    private fun configureToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

/*
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            */
/*
            val lat: Double = Math.round(location.latitude*1000.0)/1000.0
            val long: Double = Math.round(location.longitude*1000.0)/1000.0
            val userLocation = UserLocation(lat, long, dbValues.uuid)
            *//*

            dbReferences.locReference.setValue(location)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initiate the Settings Manager
        sManager = SettingsManager(this::getUserNumber)
        sManager.initEditTexts( editDisplayName, editUsername, editEmail, editPhoneNum )

        // Configure the toolbar
        toolbarTextView = findViewById(R.id.toolbarTextView) as TextView
        spinner = findViewById(R.id.spinner) as Spinner
        configureToolbar()
        toolbarTextView!!.visibility = View.VISIBLE

/*        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
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
        }*/

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
                } else {
                    sManager.initEditTexts(editEmail, editPhoneNum)
                    sManager.updateProfile(this@SettingsActivity, editDisplayName, editPhoneNum, editUsername)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@SettingsActivity, "Error: Failed to read user data", Toast.LENGTH_LONG).show()
            }
        }

        //define a ValueEventListener
        val contactsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // if dataSnapshot exists
                if (dataSnapshot.exists()) {
                    val list = dataSnapshot.getValue(ContactList::class.java)
                    if (list != null) {
                        sManager.contactList = list
                        sManager.initEmergencyContacts(arrayListOf(contact1, contact2, contact3, contact4, contact5, contact6))
                        dLog(TAG, "Contacts Exists")
                    } else {
                        sManager.initEmergencyContacts(arrayListOf(contact1, contact2, contact3, contact4, contact5, contact6))
                        sManager.updateContacts(this@SettingsActivity)
                        Toast.makeText(this@SettingsActivity, "ContactList is Null", Toast.LENGTH_LONG).show()
                    }
                } else {
                    sManager.initEmergencyContacts(arrayListOf(contact1, contact2, contact3, contact4, contact5, contact6))
                    sManager.updateContacts(this@SettingsActivity)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@SettingsActivity, "Error: Failed to read emergency contact data", Toast.LENGTH_LONG).show()
            }
        }

        // add listener to reference
        dbReferences.userReference.addValueEventListener(profileListener)
        dbReferences.contactsReference.addValueEventListener(contactsListener)

        // set global listener to the listener defined
        this.profileListener = profileListener
        this.contactsListener = contactsListener
    }

    override fun onStop() {
        //remove the listener
        dbReferences.userReference.removeEventListener(profileListener!!)
        dbReferences.contactsReference.removeEventListener(contactsListener!!)
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
            else -> return false
        }
    }
}

