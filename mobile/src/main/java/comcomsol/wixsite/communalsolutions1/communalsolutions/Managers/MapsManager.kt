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
import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.DBReferences
import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.DBValues
import android.app.Activity
import android.content.Context
import android.os.Parcelable
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.UserLocation
import comcomsol.wixsite.communalsolutions1.communalsolutions.R.drawable.profile

class MapsManager(private val context: Context, private val mMap: GoogleMap, private val activity: Activity) {
    // Database
//    private val dbValues = DBValues()
    private val dbReferences = DBReferences()
    private var location: UserLocation = UserLocation()

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
            moveMyLocation()
            if (!centered) {
                centerCamera(getLatLng(), 15f)
                centered = true
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    init {
        dbReferences.locReference.addValueEventListener(cameraLocationListener)

        locationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager?
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

    fun moveMyLocation() {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(getLatLng()).title("My Location"))
    }

    fun centerCamera(location: LatLng, zoom: Float?) {
        if (zoom == null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
        }
    }

    fun centerMe(location: LatLng) { centerCamera(location, null) }

    fun getLatLng(): LatLng { return LatLng(location.latitude, location.longitude) }
}