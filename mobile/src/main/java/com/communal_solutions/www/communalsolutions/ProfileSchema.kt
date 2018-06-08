package com.communal_solutions.www.communalsolutions

class ProfileSchema(username: String, profileName: String, phoneNum: String, status: String){

    private var dbHandler: DatabaseHandler = DatabaseHandler()

    // profile info
    private var uid = -1
    private var username = ""
    private var profileName = ""
    private var phoneNum = ""
    private var status = ""

    init {
        this.username = username
        this.profileName = profileName
        this.phoneNum = phoneNum
        this.status = status
    }

    // list of emergency contacts and location
    private var contacts = arrayOfNulls<String>(6)
    private var gpsCoords = arrayOfNulls<String>(2)

    // Get Data
    fun getUsername(): String { return username }
    fun getProfileName(): String { return profileName }
    fun getPhoneNum(): String { return phoneNum }
    fun getStatus(): String { return status }
    fun getContact(which: Int): String { return if (contacts[which] != null) contacts[which]!! else "null" }

    // Set Data
    fun updateUsername(newUsername: String) { username = newUsername }
    fun updateProfileName(newProfileName: String) { profileName = newProfileName }
    fun updatePhoneNum(newPhoneNumber: String) { phoneNum = newPhoneNumber }
    fun updateStatus(newStatus: String) { status = newStatus }
    fun updateContact(which: Int, contactName: String) {
        contacts[which] = contactName
    }

}