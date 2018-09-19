package comcomsol.wixsite.communalsolutions1.communalsolutions.VirtualObjects

import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.DBValues

/*
Profile describes the user's profile and all values associated with it
 */

class Profile() {
    // profile info
    var profile_name: String = ""
    var user_name: String = ""
    var cell_number: String = ""
    var email_address: String = ""
    var uuid: String = ""
        private set

    constructor(dbValues: DBValues, cell: String): this() {
        profile_name = "" //"random_profile_name${Math.abs(dbValues.uuid.hashCode())}"
        email_address = dbValues.user!!.email!!
        user_name = dbValues.user.email!!.substringBefore('@', "")
        cell_number = cell
        uuid = dbValues.uuid
    }

    constructor(dbValues: DBValues, name: String, user: String, cell: String): this() {
        profile_name = name
        user_name = user
        cell_number = cell
        email_address = dbValues.user!!.email!!
        uuid = dbValues.uuid
    }

    override fun toString(): String {
            return "Profile(profile_name='$profile_name', user_name='$user_name', cell_number='$cell_number', email_address='$email_address', uuid='$uuid')"
    }
}