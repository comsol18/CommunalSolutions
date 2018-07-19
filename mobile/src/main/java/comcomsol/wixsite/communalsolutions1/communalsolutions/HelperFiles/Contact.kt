package comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles

class Contact() {
    var cName: String = "No Contact"
    var cPhoneNum: String = "No Number"

    constructor(name: String, phone: String) : this() {
        cName = name
        cPhoneNum = phone
    }

    override fun toString(): String {
        return "Contact(cName='$cName', cPhoneNum='$cPhoneNum')"
    }
}