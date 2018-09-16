package comcomsol.wixsite.communalsolutions1.communalsolutions

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.Manifest
import android.content.DialogInterface
import android.content.res.Configuration
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.widget.*
import com.google.android.gms.maps.*
import kotlinx.android.synthetic.main.toolbar.*
import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.*
import comcomsol.wixsite.communalsolutions1.communalsolutions.Managers.MapsManager
import comcomsol.wixsite.communalsolutions1.communalsolutions.VirtualObjects.MapSearchTypes
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.coroutines.experimental.bg
import kotlin.concurrent.thread
import android.R.string.ok
import android.net.Uri
import android.os.AsyncTask
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch


/*import com.google.android.gms.location.places.GeoDataClient
import android.support.v4.app.FragmentActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.PlaceDetectionClient*/

// The Home Activity is where the user may search for places on the map and call for the police if necessary.

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = "HomeActivity"
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var drawer_layout: DrawerLayout? = null
    private var memergency = false
    private var mhospital = false
    private var mpolice = false
    private var mdefense = false

    // Managers
    private var mapsManager: MapsManager? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_profile-> {
                loadSettings()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_emergency-> {
                callPolice()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_center-> {
                mapsManager!!.clearMap()
                if (mapsManager!!.markerAdder != null) {
                    try {
                        mapsManager!!.markerAdder!!.execute()
                    } catch (e: IllegalStateException) {}
                }
                return@OnNavigationItemSelectedListener true
            }
            else -> return@OnNavigationItemSelectedListener false
        }
    }

    inner class CountdownDialog {
        private val dialog: AlertDialog
        private val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this@HomeActivity)
                .setMessage("Calling police in 10 seconds")
                .setTitle("Call the Police?")

        private fun makeCall() {
            val MY_PERMISSIONS_REQUEST= 9002
            // TODO NOTE*** Only uncomment the following line if you want the app to call 911, otherwise replace 911 with a different number or leave commented ***
//            val POLICE_NUMBER = "tel:911"
            val POLICE_NUMBER = "tel:5555555555"

            val callIntent = Intent(Intent.ACTION_CALL, Uri.parse(POLICE_NUMBER))
            if (ActivityCompat.checkSelfPermission(this@HomeActivity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) startActivity(callIntent)
            else {
                ActivityCompat.requestPermissions(this@HomeActivity,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        MY_PERMISSIONS_REQUEST)
                startActivity(callIntent)
            }
        }

        fun startDialog() {
            dialog.show()
            while (dialog.isShowing) {

            }

        }

        init {
            dialogBuilder.setPositiveButton("Call Now", DialogInterface.OnClickListener { dialog, id -> makeCall() })
            dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> dialog.dismiss() })
            dialog = dialogBuilder.create()
        }

    }

    // TODO:: right now, countdown will not cease if canceled. Be sure to fix.
    private fun callPolice() {

        val countDownThread: (AlertDialog) -> Thread = {
            thread {
                for (sec in 9 downTo 1) {
                    Thread.sleep(1000)
                    it.setMessage("Calling police in $sec ${if (sec>1) "seconds" else "second"}")
                }
                Thread.sleep(1000)
                it.setMessage("Calling the police now")
                Thread.sleep(500)
                it.cancel()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        getPermissions()
        configureToolbar()
        configureNavigationDrawer()
        configureBottomDrawer()

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
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }

    private fun configureNavigationDrawer() {
        drawer_layout = findViewById(R.id.drawer_layout) as DrawerLayout
    }

    private fun configureToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun configureBottomDrawer() {
        mapPlaces.choiceMode = ListView.CHOICE_MODE_SINGLE
        val items = arrayListOf("Clinics", "Hospitals", "Police Stations", "Self Defense Classes")
        val itemAdapter = ArrayAdapter<String>(this, R.layout.map_row_item, R.id.mapSearchItem, items)
        mapPlaces.adapter = itemAdapter
        mapPlaces.onItemClickListener = AdapterView.OnItemClickListener { adapterView: AdapterView<*>, view: View, i: Int, l: Long ->
            val item = (view as TextView).text.toString()
            when (item) {
                items[0] -> mapsManager!!.markerAdder!!.mapSearchType = MapSearchTypes.Clinic
                items[1] -> mapsManager!!.markerAdder!!.mapSearchType = MapSearchTypes.Hospital
                items[2] -> mapsManager!!.markerAdder!!.mapSearchType = MapSearchTypes.Police
                items[3] -> mapsManager!!.markerAdder!!.mapSearchType = MapSearchTypes.SelfDefenseCourse
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapsManager = MapsManager(this, googleMap, this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.navigationmenu, menu)
        return true
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
            R.id.emergency-> {
                when (memergency) {
                    true -> memergency = false
                    false -> memergency = true
                }
                item.setChecked(memergency)
            }
            R.id.hospital -> {
                when (mhospital) {
                    true -> mhospital = false
                    false -> mhospital = true
                }
                item.setChecked(mhospital)
            }
            R.id.police -> {
                when (mpolice) {
                    true -> mpolice = false
                    false -> mpolice = true
                }
                item.setChecked(mpolice)
            }
            R.id.defense -> {
                when (mdefense) {
                    true -> mdefense = false
                    false -> mdefense = true
                }
                item.setChecked(mdefense)
            }
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
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@HomeActivity,
                    arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS),
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
