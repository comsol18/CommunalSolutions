package com.communal_solutions.www.communalsolutions

import com.google.firebase.database.FirebaseDatabase

class DatabaseHandler {

    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val userRef = db.getReference()

    fun createNewUser(user: ProfileSchema) {
        // TODO: userRef.child("users").child(
    }

    fun getDisplayName(): String {

    }

    fun getEmail(): String {

    }

    fun getPhoneNumber(): String {

    }

    fun getUserName(): String {

    }

}