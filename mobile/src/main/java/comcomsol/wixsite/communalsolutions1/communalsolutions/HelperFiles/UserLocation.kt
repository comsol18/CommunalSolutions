package comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles

import android.location.Location
import com.google.android.gms.maps.model.LatLng

class UserLocation() {
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor(location: Location): this() {
        this.latitude = location.latitude
        this.longitude = location.longitude
    }
}