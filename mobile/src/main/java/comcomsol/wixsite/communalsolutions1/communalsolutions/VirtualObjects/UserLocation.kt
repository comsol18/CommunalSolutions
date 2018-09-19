package comcomsol.wixsite.communalsolutions1.communalsolutions.VirtualObjects

import android.location.Location

class UserLocation() {
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor(location: Location): this() {
        this.latitude = location.latitude
        this.longitude = location.longitude
    }
}