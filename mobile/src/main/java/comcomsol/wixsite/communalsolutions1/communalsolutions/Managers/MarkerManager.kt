package comcomsol.wixsite.communalsolutions1.communalsolutions.Managers

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.*
import comcomsol.wixsite.communalsolutions1.communalsolutions.VirtualObjects.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset

class MarkerAdder(private val context: Context, private val mMap: GoogleMap, private val location: LatLng): AsyncTask<Void, Void, ArrayList<LocalPlace>>() {
    private val TAG = "MarkerAdder"
    private var jsonPlaces: JSONArray = JSONArray()
    private var places: ArrayList<LocalPlace> = ArrayList()
    private lateinit var queryUri: Uri
    lateinit var mapSearchType: MapSearchTypes
    var numListings = 5
    var miles = 5

    private fun readJSONFromUri(): JSONArray {
        val inputStream: InputStream = URL(queryUri.toString()).openStream()
        try {
            val rd = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
            val jsonText = readAll(rd)
            return JSONObject(jsonText).getJSONArray("results")
        } finally {
            inputStream.close()
        }
    }

    private fun addMarkers(places: ArrayList<LocalPlace>) {
        dLog(TAG, "Places size: ${places.size}")
        val len = if (numListings < places.size) numListings else places.size
        for (item in 0 until len) {
            mMap.addMarker(MarkerOptions()
                    .position(places[item].location)
                    .title(places[item].name)
            )
        }
    }

    @Throws(RuntimeException::class)
    private fun buildQuery() {
        val QUERY_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
        val keyword =
                when (mapSearchType) {
                    MapSearchTypes.Clinic -> "health clinic"
                    MapSearchTypes.Hospital -> "hospital"
                    MapSearchTypes.Police -> "police"
                    MapSearchTypes.SelfDefenseCourse -> "self defense"
                    else -> {
                        dLog(TAG, "Error Building Query")
                        "error"
                    }
                }

        dLog(TAG, keyword)

        // build URI
        val queryUri: Uri = Uri.parse(QUERY_URL).buildUpon()
                .appendQueryParameter("location", "${location.latitude},${location.longitude}")
                .appendQueryParameter("radius", "${miles*1609}")
                .appendQueryParameter("keyword", keyword)
                .appendQueryParameter("key", "AIzaSyA4RWZqvcpQxZWSxtmAtG_1dB3gG26FvdQ")
                .build()
        dLog(TAG, queryUri.toString())
        this.queryUri = queryUri
    }

    // AsyncTask execute functions
    override fun doInBackground(vararg params: Void?): ArrayList<LocalPlace> {
        try {
            buildQuery()
        } catch (e: RuntimeException) {
//            Toast.makeText(context, "Please select a search option.", Toast.LENGTH_SHORT).show()
            return ArrayList()
        }
        jsonPlaces = readJSONFromUri()
        for (item in 0 until numListings) {
            val newPlace =
                    when (mapSearchType) {
                        MapSearchTypes.Hospital -> Hospital(jsonPlaces.getJSONObject(item), location)
                        MapSearchTypes.Police -> Police(jsonPlaces.getJSONObject(item), location)
                        MapSearchTypes.SelfDefenseCourse -> SelfDefenseCourse(jsonPlaces.getJSONObject(item), location)
                        else -> Clinic(jsonPlaces.getJSONObject(item), location)
                    }
            places.add(newPlace)
        }
        return places
    }

    override fun onPostExecute(result: ArrayList<LocalPlace>?) {
        addMarkers(result!!)
        super.onPostExecute(result)
    }
}

class MarkerManager {
}