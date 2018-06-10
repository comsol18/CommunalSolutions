package com.communal_solutions.www.communalsolutions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class DatabaseHandler {

    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val userRef = db.getReference()

    fun createNewUser(user: ProfileSchema) {
        // TODO: userRef.child("users").child(
    }

    fun getDisplayName(): String {
        userRef.
    }

    fun getEmail(): String {
        return ""

    }

    fun getPhoneNumber(): String {
        return ""

    }

    fun getUserName(): String {
        return ""

    }

}