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
    private var db: FirebaseDatabase
    private var uid: String
    private var cUser: FirebaseUser?
    private var dbReference: DatabaseReference
    private var dbPrivateReferences: DBPrivateReferences

    fun getUID(): String { return uid }
    fun getUser(): FirebaseUser? { return cUser }

    fun writeProfileData(profile: Profile, contactList: ContactList) {
        getReference("users")!!.setValue(profile)
        getReference("contacts")!!.setValue(contactList)
    }

    fun getReference(ref: String): DatabaseReference? {
        return when (ref) {
            "users" -> dbPrivateReferences.userReference.child(uid)
            "locations" -> dbPrivateReferences.locReference.child(uid)
            "contacts" -> dbPrivateReferences.contactsReference.child(uid)
            "private" -> dbPrivateReferences.dbPrivate!!
            else -> null
        }
    }

    init {
        db = FirebaseDatabase.getInstance()
        dbReference = db.reference
        dbPrivateReferences = DBPrivateReferences(dbReference.child("private"))
        cUser = FirebaseAuth.getInstance().currentUser
        uid = cUser!!.uid.hashCode().toString()
    }
}