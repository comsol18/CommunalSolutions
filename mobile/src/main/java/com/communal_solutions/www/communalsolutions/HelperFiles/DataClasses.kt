package com.communal_solutions.www.communalsolutions.HelperFiles

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

data class DBPrivateReferences(
        val dbPrivate: DatabaseReference? = null,
        val userReference: DatabaseReference = dbPrivate!!.child("users"),
        val locReference: DatabaseReference = dbPrivate!!.child("locations"),
        val contactsReference: DatabaseReference = dbPrivate!!.child("contacts")
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

data class Contact(
        val cName: String = "",
        val cPhoneNum: String = ""
)

data class ContactList(
        var contact1: Contact = Contact(),
        var contact2: Contact = Contact(),
        var contact3: Contact = Contact(),
        var contact4: Contact = Contact(),
        var contact5: Contact = Contact(),
        var contact6: Contact = Contact()
)
