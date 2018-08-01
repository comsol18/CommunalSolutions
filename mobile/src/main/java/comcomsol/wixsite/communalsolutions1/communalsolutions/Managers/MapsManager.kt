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
import com.google.android.gms.common.GoogleApiAvailability
import android.net.Uri
import android.support.v4.app.FragmentActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import org.json.JSONObject


class MapsManager(private val context: Context, private val mMap: GoogleMap, private val activity: Activity): SeekBar.OnSeekBarChangeListener {

    private val TAG = "MapsManager"

    // Database
//    private val dbValues = DBValues()
    private val dbReferences = DBReferences()
    private var location: UserLocation = UserLocation()
    private val seekBar = activity.radiusSeekBar
    private val playVersion = activity.packageManager.getPackageInfo(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, 0).versionCode
    var searchRadius = 5
    val mapActivity: MapActivity = this.MapActivity()

    // Managers
    private val locationManager: LocationManager?

    // Listeners
    private var centered = false
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            dbReferences.locReference.setValue(UserLocation(location))
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

    init {
        configSeekBar()
        dbReferences.locReference.addValueEventListener(cameraLocationListener)
        locationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager?
        mMap.isMyLocationEnabled = true

//        dLog(TAG, "Google Play Version: $playVersion")

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

    private fun configSeekBar() {
        seekBar.min = 10
        seekBar.max = 100
        seekBar.setOnSeekBarChangeListener(this)
    }

/*    fun queryPlaces(vararg keywords: String): JsonObjectRequest? {
        val QUERY_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
        var keyword = ""
        for (word in keywords) keyword = if (keyword == "") word else "$keyword,$word"
        val queryUri: Uri = Uri.parse(QUERY_URL).buildUpon()
                .appendQueryParameter("location", "${location.latitude},${location.longitude}")
                .appendQueryParameter("radius", "${searchRadius*1609}")
                .appendQueryParameter("keyword", keyword)
                .appendQueryParameter("key", "AIzaSyCYrJBnL2QDUL3xUuSXXoM-YkpSpC42rdE")
                .build()
        return JsonObjectRequest(Request.Method.GET, queryUri.toString(), null,
                Response.Listener { response -> *//*activity.jsonData.text = response.toString()*//* },
                Response.ErrorListener { error -> }
        )
    }*/

    fun centerCamera(location: LatLng, zoom: Float?) {
        if (zoom == null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
        }
    }

    fun getLatLng(): LatLng { return LatLng(location.latitude, location.longitude) }

    // SeekBar.OnSeekBarChangeListener interface functions
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        searchRadius = progress
        activity.radius.text = "$searchRadius miles"
    }
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    inner class MapActivity: FragmentActivity(), GoogleApiClient.OnConnectionFailedListener {
        override fun onConnectionFailed(p0: ConnectionResult) {
        }
    }
}