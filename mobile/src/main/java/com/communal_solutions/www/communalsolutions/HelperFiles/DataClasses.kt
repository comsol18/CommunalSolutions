package com.communal_solutions.www.communalsolutions.HelperFiles

import android.provider.ContactsContract
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

data class DBReferences(
        val dbReference: DatabaseReference? = null,
        val uid: String = "",
        val userReference: DatabaseReference = dbReference!!.child("users").child(uid),
        val locReference: DatabaseReference = dbReference!!.child("locations").child(uid),
        val contactsReference: DatabaseReference = dbReference!!.child("contacts").child(uid),
        val debugReference: DatabaseReference = dbReference!!.child("debug").child(uid)
)

data class DBPublicReferences(
        val database: FirebaseDatabase = FirebaseDatabase.getInstance(),
        val dbReference: DatabaseReference = database.reference.child("public")
)

data class UserLocation(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        var uuid: String = ""
)

data class ContactList(
        var contact1: Contact = Contact(),
        var contact2: Contact = Contact(),
        var contact3: Contact = Contact(),
        var contact4: Contact = Contact(),
        var contact5: Contact = Contact(),
        var contact6: Contact = Contact()
)
