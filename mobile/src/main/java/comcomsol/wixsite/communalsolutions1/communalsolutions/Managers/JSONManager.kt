package comcomsol.wixsite.communalsolutions1.communalsolutions.Managers

import com.google.android.gms.maps.model.LatLng
import comcomsol.wixsite.communalsolutions1.communalsolutions.VirtualObjects.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.json.JSONArray

class JSONManager(private val userLocation: LatLng) {
    private val TAG = "JSONManager"

    fun jsonPlaceSearch(placesArray: JSONArray, placesType: MapSearchTypes, addMarkers: (LocalPlace) -> Boolean): ArrayList<LocalPlace> {
        val list: ArrayList<LocalPlace> = ArrayList()
        for (item in 0 until placesArray.length()) {
            val newPlace =
                    when (placesType) {
                        MapSearchTypes.Hospital -> Hospital(placesArray.getJSONObject(item), userLocation)
                        MapSearchTypes.Police -> Police(placesArray.getJSONObject(item), userLocation)
                        MapSearchTypes.SelfDefenseCourse -> SelfDefenseCourse(placesArray.getJSONObject(item), userLocation)
                        else -> Clinic(placesArray.getJSONObject(item), userLocation)
                    }

            list.add(newPlace)
            launch(CommonPool){ addMarkers(newPlace) }
        }
        return list
    }
}
