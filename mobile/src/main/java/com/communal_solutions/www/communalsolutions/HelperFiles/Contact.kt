package com.communal_solutions.www.communalsolutions.HelperFiles

class Contact() {
    var cName: String = ""
    var cPhoneNum: String = ""
    var cPhoto: String = ""

    constructor(name: String, phone: String) : this() {
        cName = name
        cPhoneNum = phone
        cPhoto = cPhoneNum.substringAfter("+").substringBefore("-")
    }

    override fun toString(): String {
        return "Contact(cName='$cName', cPhoneNum='$cPhoneNum', cPhoto=$cPhoto)"
    }
}