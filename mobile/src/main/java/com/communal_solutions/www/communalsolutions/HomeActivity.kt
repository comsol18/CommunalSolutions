package com.communal_solutions.www.communalsolutions

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.telephony.TelephonyManager
import android.Manifest
import android.content.res.Configuration
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.communal_solutions.www.communalsolutions.HelperFiles.*
import com.communal_solutions.www.communalsolutions.Managers.DatabaseManager


class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private val dbManager = DatabaseManager()
    private var profileListener: ValueEventListener? = null
    private var passProfile: Profile? = null
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var drawer_layout: DrawerLayout? = null
    private lateinit var mMap: GoogleMap
    private var memergency = false;
    private var mhospital = false;
    private var mpolice=false;
    private var mdefense = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
        getPermissions()

        val profileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // if dataSnapshot exists
                if (dataSnapshot.exists()) {
                    val profile = dataSnapshot.getValue(Profile::class.java)
                    dLog("HomeActivity.profileListener", "profileIsNull=${profile==null}")
                    passProfile = profile
                } else eLog("HomeActivity.profileListener", "dataSnapshot DNE")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Error: Failed to read user data", Toast.LENGTH_LONG).show()
            }
        }

        // add listener to reference
        dbManager.getReference("users").addValueEventListener(profileListener)
        this.profileListener = profileListener

        configureNavigationDrawer()
        configureToolbar()

        drawer_layout = findViewById(R.id.drawer_layout) as DrawerLayout
        mDrawerToggle = object : ActionBarDrawerToggle(this, drawer_layout, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                Log.d("demo", "onDrawerClosed: $title")

                invalidateOptionsMenu()
            }
        }

        drawer_layout!!.setDrawerListener(mDrawerToggle)

        // for straight to settings
        //loadSettings()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.0f))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu_contacts, menu)
        return true
    }

    private fun configureNavigationDrawer() {
        drawer_layout = findViewById(R.id.drawer_layout) as DrawerLayout
    }

    private fun configureToolbar() {
        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_lock_black_24dp)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        val itemId: Int = item.itemId
        when (itemId) {
            R.id.commInfo -> {}
            R.id.commEvents -> {}
            R.id.settings -> loadSettings()
            R.id.logOut -> logout()
            R.id.emergency->
                if(item.isChecked()){
                item.setChecked(false);
                memergency = false;
                }
            else {
                // If item is unchecked then checked it
                item.setChecked(true);
                memergency = true;
            }
            R.id.hospital ->
            if (item.isChecked()) {
                // If item already checked then unchecked it
                item.setChecked(false);
                mhospital = false;
            } else {
                // If item is unchecked then checked it
                item.setChecked(true);
                mhospital = true;
            }
            R.id.police ->
                if (item.isChecked()) {
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    mpolice = false;
                } else {
                    // If item is unchecked then checked it
                    item.setChecked(true);
                    mpolice = true;
                }
            R.id.defense ->
                if (item.isChecked()) {
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    mdefense = false;
                } else {
                    // If item is unchecked then checked it
                    item.setChecked(true);
                    mdefense = true;
                }

        }
        return if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
        // Handle your other action bar items...
    }

    private fun getPermissions() {
        val MY_PERMISSIONS_REQUEST= 9002
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@HomeActivity,
                    arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS),
                    MY_PERMISSIONS_REQUEST)
        }
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
        val hashCode: Int = dbManager.cUser!!.uid.hashCode()
        if (passProfile != null) {
            dLog("HomeActivity.loadSettings", "passProfile is not null")
            settingsIntent.putExtra("display_name", passProfile!!.profile_name)
            settingsIntent.putExtra("email", passProfile!!.email_address)
            settingsIntent.putExtra("user_name", passProfile!!.user_name)
            settingsIntent.putExtra("phone_number", getUserNumber())
            //settingsIntent.putExtra("status", passProfile!!.status)
        } else {
            eLog("HomeActivity.loadSettings", "passProfile is null")
            settingsIntent.putExtra("display_name", "random_profile_name${Math.abs(hashCode.hashCode())}")
            settingsIntent.putExtra("email", dbManager.cUser!!.email)
            settingsIntent.putExtra("user_name", dbManager.cUser!!.email!!.substringBefore('@', ""))
            settingsIntent.putExtra("phone_number", getUserNumber())
            //settingsIntent.putExtra("status", "")
        }
        startActivity(settingsIntent)
    }

    fun logout() {
        dbManager.getReference("users").removeEventListener(profileListener!!)
        FirebaseAuth.getInstance().signOut()
        super.finish()
    }

}
