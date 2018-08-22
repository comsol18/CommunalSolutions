package comcomsol.wixsite.communalsolutions1.communalsolutions.Managers

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v13.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.*
import android.app.Activity
import android.content.Context
import android.widget.SeekBar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_home.*
import android.net.Uri
import android.os.AsyncTask
import android.os.StrictMode
import android.util.JsonReader
import android.widget.Toast
import com.google.android.gms.maps.model.MarkerOptions
import comcomsol.wixsite.communalsolutions1.communalsolutions.R.id.*
import comcomsol.wixsite.communalsolutions1.communalsolutions.VirtualObjects.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.coroutines.experimental.bg
import java.net.URL
import java.nio.charset.Charset
import org.json.*
import java.io.*

class MapsManager(context: Context, private val mMap: GoogleMap, private val activity: Activity): SeekBar.OnSeekBarChangeListener {

    // Other Variables and Values
    private val TAG = "MapsManager"
    var markerAdder: MarkerAdder? = null

    // Database
    private val dbReferences = DBReferences()
    private var location: UserLocation = UserLocation()
    private val radiusSeekBar = activity.radiusSeekBar
    private val listingsSeekBar = activity.numListingsSeekbar
    var searchRadius = 4
    var numListings = 5

    // Managers
    private val locationManager: LocationManager?

    // Listeners
    private var centered = false
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            dbReferences.locReference.setValue(UserLocation(location))
            markerAdder = MarkerAdder(context, mMap, LatLng(location.latitude, location.longitude))
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private val cameraLocationListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // if dataSnapshot exists
            if (dataSnapshot.exists()) {
                location = dataSnapshot.getValue(UserLocation::class.java)!!
            }
            if (!centered) {
                centerCamera(getLatLng(), 15f)
                centered = true
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    // Public functions
    fun clearMap() { mMap.clear() }

    fun centerCamera(location: LatLng, zoom: Float?) {
        if (zoom == null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
        }
    }

    fun getLatLng(): LatLng { return LatLng(location.latitude, location.longitude) }

    // private functions
    private fun configSeekBars() {
        radiusSeekBar.progress = searchRadius
        radiusSeekBar.max = 19
        radiusSeekBar.setOnSeekBarChangeListener(this)

        listingsSeekBar.progress = numListings
        listingsSeekBar.max = 15
        listingsSeekBar.setOnSeekBarChangeListener(this)
    }

    // Overridden functions
    // SeekBar.OnSeekBarChangeListener interface functions
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar!!) {
            radiusSeekBar -> {
                searchRadius = progress + 1
                activity.radius.text = "$searchRadius miles"
            }
            listingsSeekBar -> {
                numListings = progress + 5
                activity.totalNumListings.text = "$numListings listings"
            }
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (markerAdder != null) {
            when (seekBar!!) {
                radiusSeekBar -> markerAdder!!.miles = searchRadius
                listingsSeekBar -> markerAdder!!.numListings = numListings
            }
//            markerAdder!!.buildQuery()
        }
    }

    // Initialization
    init {
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        configSeekBars()
        dbReferences.locReference.addValueEventListener(cameraLocationListener)
        locationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager?
        mMap.isMyLocationEnabled = true

        val MY_PERMISSIONS_REQUEST = 9002
        try {
            // Request location updates
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity,
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
}