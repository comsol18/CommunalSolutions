package com.communal_solutions.www.communalsolutions.Managers

import com.communal_solutions.www.communalsolutions.HelperFiles.ContactList

class ContactManager {
    private val contactList = ContactList()
    fun getContacts(): ContactList {
        return contactList
    }
}