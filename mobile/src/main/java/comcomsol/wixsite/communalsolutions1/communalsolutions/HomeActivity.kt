package comcomsol.wixsite.communalsolutions1.communalsolutions

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
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.*
import comcomsol.wixsite.communalsolutions1.communalsolutions.Managers.MapsManager


class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var drawer_layout: DrawerLayout? = null

    // Managers
    private var mapsManager: MapsManager? = null

    // Database
    private val dbValues = DBValues()
    private val dbReferences = DBReferences()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        getPermissions()

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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapsManager = MapsManager(this, googleMap, this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.navigationmenu, menu)
        return true
    }

    private fun configureNavigationDrawer() {
        drawer_layout = findViewById(R.id.drawer_layout) as DrawerLayout
    }

    private fun configureToolbar() {
        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.setHomeAsUpIndicator(R.drawable.ic_lock_black_24dp)
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeButtonEnabled(true)
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
/*
            R.id.commInfo -> {
                val commInfoIntent = Intent(this@HomeActivity, CommunityInfoActivity::class.java)
                startActivity(commInfoIntent)
            }
            R.id.commEvents -> {
                val commInfoIntent = Intent(this@HomeActivity, CommunityInfoActivity::class.java)
                startActivity(commInfoIntent)
            }
*/
            R.id.settings -> loadSettings()
            R.id.logOut -> logout()
            else -> {}
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

    private fun loadSettings() {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        super.finish()
    }

}
