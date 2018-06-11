package com.communal_solutions.www.communalsolutions

import com.google.firebase.auth.FirebaseAuth

class ProfileSchema(profileName: String, phoneNum: String, status: String){

    // profile info
    var profileName = ""
    var username = ""
    var phoneNum = ""
    var eMail = ""
    var status = ""

    /* list of emergency contacts and location
    private var contacts = arrayOfNulls<String>(6)
    private var gpsCoords = arrayOfNulls<String>(2)
    */

    init {
        this.eMail = FirebaseAuth.getInstance().currentUser!!.email.toString()
        this.username = this.eMail.substringBefore('@', "")
        this.profileName = profileName
        this.phoneNum = phoneNum
        this.status = status
    }

}