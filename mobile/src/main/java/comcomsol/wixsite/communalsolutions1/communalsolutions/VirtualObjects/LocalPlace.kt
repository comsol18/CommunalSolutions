package comcomsol.wixsite.communalsolutions1.communalsolutions.VirtualObjects

import android.net.Uri
import android.os.StrictMode
import com.google.android.gms.maps.model.LatLng
import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset

class Hospital(jsonPlace: JSONObject, userLocation: LatLng): LocalPlace(jsonPlace, userLocation) {
    override var TAG: String = "Hospital"
    override var searchType: MapSearchTypes = MapSearchTypes.Hospital
}

class Police(jsonPlace: JSONObject, userLocation: LatLng): LocalPlace(jsonPlace, userLocation) {
    override var TAG: String = "Police"
    override var searchType: MapSearchTypes = MapSearchTypes.Police
}

class Clinic(jsonPlace: JSONObject, userLocation: LatLng): LocalPlace(jsonPlace, userLocation) {
    override var TAG: String = "Clinic"
    override var searchType: MapSearchTypes = MapSearchTypes.Clinic
}

class SelfDefenseCourse(jsonPlace: JSONObject, userLocation: LatLng): LocalPlace(jsonPlace, userLocation) {
    override var TAG: String = "SelfDefenseCourse"
    override var searchType: MapSearchTypes = MapSearchTypes.SelfDefenseCourse
}

class EmergencyContact() {
    var TAG: String = "EmergencyContact"
    var searchType: MapSearchTypes = MapSearchTypes.EmergencyContact
    var location: LatLng? = null
}

abstract class LocalPlace(private val jsonPlace: JSONObject, private val userLocation: LatLng) {
    // abstract variables
    abstract var TAG: String
    abstract var searchType: MapSearchTypes
//    var location: LatLng = LatLng(0.0, 0.0)
    var location: () -> LatLng = {
        val coordinates= jsonPlace.getJSONObject("geometry").getJSONObject("location").toString()
        val lat: Double = coordinates.substringAfter("lat\":").substringBefore(',').toDouble()
        val long: Double = coordinates.substringAfter("lng\":").substringBefore('}').toDouble()
//        dLog(TAG, LatLng(lat, long).toString())
        LatLng(lat, long)
    }
    val distance = distanceInMiles()

    // lambdas
    fun distanceInMiles(): Double {
        val QUERY_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?"
        val distanceUri: Uri = Uri.parse(QUERY_URL).buildUpon()
                .appendQueryParameter("units", "imperial")
                .appendQueryParameter("origins", "${userLocation.latitude},${userLocation.longitude}")
                .appendQueryParameter("destinations", "${location().latitude},${location().longitude}")
                .appendQueryParameter("key", "AIzaSyA4RWZqvcpQxZWSxtmAtG_1dB3gG26FvdQ")
                .build()

        // Connect to the URL using java's native library
        val inputStream: InputStream = URL(distanceUri.toString()).openStream()
        try {
            val rd = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
            val jsonText = readAll(rd)
            val jsonObject= JSONObject(jsonText).getJSONArray("rows").getJSONObject(0).getJSONObject("distance").toString()
            val jsonDistance = jsonObject.substringAfterLast(':').substringBeforeLast('}').toDouble()
            dLog("LocalPlace", "Distance: $jsonDistance")
            return jsonDistance
        } finally {
            inputStream.close()
        }
    }

    init {
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }
}