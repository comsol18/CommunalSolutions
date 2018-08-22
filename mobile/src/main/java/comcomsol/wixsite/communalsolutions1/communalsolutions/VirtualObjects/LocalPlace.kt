package comcomsol.wixsite.communalsolutions1.communalsolutions.VirtualObjects

import android.net.Uri
import android.os.StrictMode
import com.google.android.gms.maps.model.LatLng
import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset

enum class MapSearchTypes {
    Hospital, SelfDefenseCourse, Police, Clinic, EmergencyContact, Abstract
}

class Hospital(jsonPlace: JSONObject, userLocation: LatLng): LocalPlace(jsonPlace, userLocation) {
    override var TAG: String = "Hospital"
    override var searchType: MapSearchTypes = MapSearchTypes.Hospital
    override var distance: Double = distanceInMiles()
}

class Police(jsonPlace: JSONObject, userLocation: LatLng): LocalPlace(jsonPlace, userLocation) {
    override var TAG: String = "Police"
    override var searchType: MapSearchTypes = MapSearchTypes.Police
    override var distance: Double = distanceInMiles()
}

class Clinic(jsonPlace: JSONObject, userLocation: LatLng): LocalPlace(jsonPlace, userLocation) {
    override var TAG: String = "Clinic"
    override var searchType: MapSearchTypes = MapSearchTypes.Clinic
    override var distance: Double = distanceInMiles()
}

class SelfDefenseCourse(jsonPlace: JSONObject, userLocation: LatLng): LocalPlace(jsonPlace, userLocation) {
    override var TAG: String = "SelfDefenseCourse"
    override var searchType: MapSearchTypes = MapSearchTypes.SelfDefenseCourse
    override var distance: Double = distanceInMiles()
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
    abstract var distance: Double
    val name = jsonPlace.getString("name")
    val coordinates= jsonPlace.getJSONObject("geometry").getJSONObject("location")
    val location = LatLng(coordinates.getDouble("lat"), coordinates.getDouble("lng"))

    // lambdas
    fun distanceInMiles(): Double {
        val QUERY_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?"
        val distanceUri: Uri = Uri.parse(QUERY_URL).buildUpon()
                .appendQueryParameter("units", "imperial")
                .appendQueryParameter("origins", "${userLocation.latitude},${userLocation.longitude}")
                .appendQueryParameter("destinations", "${location.latitude},${location.longitude}")
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
        } catch (e: JSONException) {
            return 0.0
        } finally {
            inputStream.close()
        }
    }

    init {
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }
}