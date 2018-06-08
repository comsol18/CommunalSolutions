package com.communal_solutions.www.communalsolutions

class ProfileSchema {
    // profile info
    private var username = ""
    private var profileName = ""
    private var phoneNum = ""
    private var status = ""

    // list of emergency contacts and location
    private var contacts = arrayOfNulls<String>(6)
    private var gpsCoords = arrayOfNulls<String>(2)

    // Get Data
    fun getUsername(): String { return username }
    fun getProfileName(): String { return profileName }
    fun getPhoneNum(): String { return phoneNum }
    fun getStatus(): String { return status }
    fun getContact(which: Int): String { return if (contacts[which] != null) contacts[which]!! else "" }

    // Set Data
    fun setUsername(newUsername: String) { username = newUsername }
    fun setProfileName(newProfileName: String) { profileName = newProfileName }
    fun setPhoneNum(newPhoneNumber: String) { phoneNum = newPhoneNumber }
    fun setStatus(newStatus: String) { status = newStatus }
    fun setContact(which: Int, contactName: String) {

    }


}