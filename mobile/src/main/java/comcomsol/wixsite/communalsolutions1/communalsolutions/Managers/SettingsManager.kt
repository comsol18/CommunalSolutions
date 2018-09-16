package comcomsol.wixsite.communalsolutions1.communalsolutions.Managers

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.*
import comcomsol.wixsite.communalsolutions1.communalsolutions.R
import comcomsol.wixsite.communalsolutions1.communalsolutions.R.id.*
import com.google.firebase.database.*
import comcomsol.wixsite.communalsolutions1.communalsolutions.VirtualObjects.Profile

/*
The Settings manager manages the user's profile information and updates the database accordingly
 */

class SettingsManager() {

    val dbValues = DBValues()
    val dbReferences = DBReferences()

    // Fill profile object
    var profile: Profile = Profile()
    var contactList: ContactList = ContactList()

    constructor(getNumber: () -> String): this() {
        profile = Profile(dbValues, getNumber())
    }

    fun updateProfile(context: Context, name: EditText, number: EditText, uname: EditText) {
        val displayName = name.text.toString()
        val cellNumber= number.text.toString()
        val userName = uname.text.toString()

        // Validate Phone Number
        val phoneNum = validateNumber(cellNumber)
        if (TextUtils.isEmpty(phoneNum)) number.error = "InvalidNumber"

        // initilize Profile object
        profile = Profile(dbValues, displayName, userName, phoneNum)

        // push data to database
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dbReferences.userReference.setValue(profile)
                Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Error", "onCancelled: Failed to read user!")
                Toast.makeText(context, "Profile Failed To Update", Toast.LENGTH_SHORT).show()
            }
        }

        dbReferences.userReference.addListenerForSingleValueEvent(updateListener)
    }

    fun updateContacts(context: Context) {
        // push data to database
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dbReferences.contactsReference.setValue(contactList)
                Toast.makeText(context, "Emergency Contacts Updated", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Error", "onCancelled: Failed to read user!")
                Toast.makeText(context, "Emergency Contacts Failed To Update", Toast.LENGTH_SHORT).show()
            }
        }

        dbReferences.contactsReference.addListenerForSingleValueEvent(updateListener)
    }

    // Initializes EditText values
    fun initEditTexts(vararg editTexts: EditText) {
        var text: String
        for (editText in editTexts) {
            text =
                    when (editText.id) {
                        R.id.editDisplayName -> profile.profile_name
                        R.id.editUsername -> profile.user_name
                        R.id.editEmail -> profile.email_address
                        R.id.editPhoneNum -> profile.cell_number
                        else -> ""
                    }
            editText.setText(text)
        }
    }

    fun initEmergencyContacts(buttons: ArrayList<Button>) {
        for (button in buttons) {
            button.text = when (button.id) {
                contact1 -> contactList.contact1.cName
                contact2 -> contactList.contact2.cName
                contact3 -> contactList.contact3.cName
                contact4 -> contactList.contact4.cName
                contact5 -> contactList.contact5.cName
                contact6 -> contactList.contact6.cName
                else -> "No Contact"
            }
        }
    }

    // Check to see that the number is valid
    fun validateNumber(number: String): String {
        var phoneNum = number
        if(phoneNum.length == 10) {
            for (c in phoneNum) {
                if (!c.isDigit()) return ""
            }
            phoneNum = phoneNum.substring(0, 3) + "-" + phoneNum.substring(3, 6) + "-" + phoneNum.substring(6, 10)
        } else if (phoneNum.length == 12) {
            loop@ for (i in 0..11) {
                when (i) {
                    3, 7 -> {
                        if (phoneNum[i] != '-') {
                            phoneNum = ""
                            break@loop
                        }
                    }
                    else -> {
                        if (!phoneNum[i].isDigit()) {
                            phoneNum = ""
                            break@loop
                        }
                    }
                }
            }
        } else phoneNum = ""
        return phoneNum
    }
}
