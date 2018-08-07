package comcomsol.wixsite.communalsolutions1.communalsolutions.Managers

import com.google.android.gms.maps.model.LatLng
import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.MapSearchTypes
import comcomsol.wixsite.communalsolutions1.communalsolutions.VirtualObjects.*
import org.json.JSONObject
/*import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.*
import org.json.JSONArray
import android.support.design.widget.TabLayout
import com.google.gson.JsonObject
import org.json.JSONArray*/

class JSONManager(private val userLocation: LatLng) {
    private val TAG = "JSONManager"

    fun jsonPlaceSearch(placesSearch: JSONObject, placesType: MapSearchTypes): ArrayList<LocalPlace> {
        val results = placesSearch.getJSONArray("results")
        val places: ArrayList<LocalPlace> = ArrayList()
        for (item in 0 until results.length()) {
            when (placesType) {
                MapSearchTypes.Hospital -> places.add(Hospital(results.getJSONObject(item), userLocation))
                MapSearchTypes.Police -> places.add(Police(results.getJSONObject(item), userLocation))
                MapSearchTypes.SelfDefenseCourse -> places.add(SelfDefenseCourse(results.getJSONObject(item), userLocation))
                else -> places.add(Clinic(results.getJSONObject(item), userLocation))
            }
        }
        return places
    }
}
