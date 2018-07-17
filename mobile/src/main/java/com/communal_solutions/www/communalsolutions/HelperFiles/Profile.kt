package com.communal_solutions.www.communalsolutions.HelperFiles

import android.util.Log

class Profile() {
    val TAG = "Profile"
    // profile info
    var profile_name: String = ""
    var user_name: String = ""
    var cell_number: String = ""
    var email_address: String = ""
    var uuid: String = ""
        private set
    //private val status: String = ""

    constructor(profile: String): this() {
        profile_name = getToStringValue("profile_name", profile)
        user_name = getToStringValue("user_name", profile)
        cell_number = getToStringValue("cell_number", profile)
        email_address = getToStringValue("email_address", profile)
        uuid = getToStringValue("uuid", profile)
    }

    constructor(profile: String, user: String, cell: String, email: String, uuid: String): this() {
        profile_name = profile
        user_name = user
        cell_number = cell
        email_address = email
        this.uuid = uuid
    }

    private fun getToStringValue(variable: String, str: String): String {
        val delimit = variable + "='"
        val value = str.substringAfter(delimit).substringBefore("'")
        Log.e(TAG, value)
        return value
    }

    override fun toString(): String {
            return "Profile(profile_name='$profile_name', user_name='$user_name', cell_number='$cell_number', email_address='$email_address', uuid='$uuid')"
    }
}