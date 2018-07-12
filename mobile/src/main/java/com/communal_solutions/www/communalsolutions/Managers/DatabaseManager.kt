package com.communal_solutions.www.communalsolutions.Managers

import com.communal_solutions.www.communalsolutions.HelperFiles.ContactList
import com.communal_solutions.www.communalsolutions.HelperFiles.DBPrivateReferences
import com.communal_solutions.www.communalsolutions.HelperFiles.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DatabaseManager {
    // database, current user, and db references
    var db: FirebaseDatabase
    var uuid: Int
    var cUser: FirebaseUser?
    var dbReference: DatabaseReference
    var dbPrivateReferences: DBPrivateReferences

    fun writeProfileData(profile: Profile) {
        getReference("users")!!.setValue(profile)
        //getReference("contacts")!!.setValue(contactList)
    }

    fun getReference(ref: String): DatabaseReference? {
        return when (ref) {
            "users" -> dbPrivateReferences.userReference.child(uuid.toString())
            "locations" -> dbPrivateReferences.locReference.child(uuid.toString())
            "contacts" -> dbPrivateReferences.contactsReference.child(uuid.toString())
            "private" -> dbPrivateReferences.dbPrivate!!
            else -> null
        }
    }

    init {
        db = FirebaseDatabase.getInstance()
        dbReference = db.reference
        dbPrivateReferences = DBPrivateReferences(dbReference.child("private"))
        cUser = FirebaseAuth.getInstance().currentUser
        uuid = cUser!!.uid.hashCode()
    }
}