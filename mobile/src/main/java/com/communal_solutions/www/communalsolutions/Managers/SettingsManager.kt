package com.communal_solutions.www.communalsolutions.Managers

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.communal_solutions.www.communalsolutions.HelperFiles.Profile
import com.communal_solutions.www.communalsolutions.R
import com.google.firebase.database.*

class SettingsManager(intent: Intent) {
    // Fill profile object
    private val dbManager = DatabaseManager()
    private var profile: Profile = Profile(
            intent.getStringExtra("display_name"),
            intent.getStringExtra("user_name"),
            intent.getStringExtra("phone_number"),
            intent.getStringExtra("email"),
            dbManager.uuid
    )

    // Reset the profile object
    fun setProfile(profile: Profile) { this.profile = profile }

    fun updateProfile(context: Context, vararg editTexts: EditText) {
        val editTextArrayList: ArrayList<EditText> = ArrayList()
        for (editText in editTexts) {
            editTextArrayList.add(editText)
        }
        val displayName = editTextArrayList[0].text.toString()
        var phoneNum = editTextArrayList[1].text.toString()
        val username = editTextArrayList[2].text.toString()
        val email = dbManager.cUser!!.email

        // Validate Phone Number
        phoneNum = validateNumber(phoneNum)
        if (TextUtils.isEmpty(phoneNum)) editTextArrayList[1].setError("InvalidNumber")

        // initilize Profile object
        profile = Profile(displayName, username, phoneNum, email!!, dbManager.uuid)
        //contactList = contactManager.getContacts()

        // push data to database
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dbManager.writeProfileData(profile)
                Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Error", "onCancelled: Failed to read user!")
                Toast.makeText(context, "Profile Failed To Update", Toast.LENGTH_SHORT).show()
            }
        }

        dbManager.getReference("users").addListenerForSingleValueEvent(updateListener)
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
