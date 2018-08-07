package comcomsol.wixsite.communalsolutions1.communalsolutions.VirtualObjects

import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.DBValues

class Profile() {
    // profile info
    var profile_name: String = ""
    var user_name: String = ""
    var cell_number: String = ""
    var email_address: String = ""
    var uuid: String = ""
        private set

    /*
    constructor(profile: String): this() {
        profile_name = getToStringValue("profile_name", profile)
        user_name = getToStringValue("user_name", profile)
        cell_number = getToStringValue("cell_number", profile)
        email_address = getToStringValue("email_address", profile)
        uuid = getToStringValue("uuid", profile)
    }
    */

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

    /*
    private fun getToStringValue(variable: String, str: String): String {
        val delimit = variable + "='"
        val value = str.substringAfter(delimit).substringBefore("'")
        Log.e("Value", value)
        return value
    }
    */

    override fun toString(): String {
            return "Profile(profile_name='$profile_name', user_name='$user_name', cell_number='$cell_number', email_address='$email_address', uuid='$uuid')"
    }
}