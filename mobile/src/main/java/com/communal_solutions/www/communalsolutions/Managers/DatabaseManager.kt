package com.communal_solutions.www.communalsolutions.Managers

import com.communal_solutions.www.communalsolutions.HelperFiles.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DatabaseManager {
    // database, current user, and db references
    var db: FirebaseDatabase
    var uuid: String
    var cUser: FirebaseUser?
    var dbReferences: DBReferences

    fun writeProfileData(profile: Profile) {
        getReference("users").setValue(profile)
        //getReference("contacts")!!.setValue(contactList)
    }

    fun getReference(ref: String): DatabaseReference {
        return when (ref) {
            "users" -> dbReferences.userReference
            "locations" -> dbReferences.locReference
            "contacts" -> dbReferences.contactsReference
            "reference" -> dbReferences.dbReference!!
            else -> dbReferences.debugReference
        }
    }

    init {
        db = FirebaseDatabase.getInstance()
        cUser = FirebaseAuth.getInstance().currentUser
        uuid = cUser!!.uid.hashCode().toString()
        dbReferences = DBReferences(db.reference.child("private"), uuid)
    }
}