package comcomsol.wixsite.communalsolutions1.communalsolutions.VirtualObjects

import android.os.Parcel
import android.os.Parcelable

/*
The Contact class describes a contact that the user can add to their ContactList
 */

class Contact(): Parcelable {
    var cName: String = "No Contact"
    var cPhoneNum: String = "No Number"

    constructor(parcel: Parcel) : this() {
        cName = parcel.readString()
        cPhoneNum = parcel.readString()
    }

    constructor(name: String, phone: String) : this() {
        cName = name
        cPhoneNum = phone
    }

    override fun toString(): String {
        return "Contact(cName='$cName', cPhoneNum='$cPhoneNum')"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cName)
        parcel.writeString(cPhoneNum)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}