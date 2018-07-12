package com.communal_solutions.www.communalsolutions

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.communal_solutions.www.communalsolutions.Adapters.ContactsAdapter
import com.communal_solutions.www.communalsolutions.Adapters.dLog
import com.communal_solutions.www.communalsolutions.Adapters.formatObject
import com.communal_solutions.www.communalsolutions.HelperFiles.Contact
import kotlinx.android.synthetic.main.activity_contacts.*

class ContactsActivity : AppCompatActivity() {

    private val TAG = "ContactsActivity"
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: ContactsAdapter
    private var sampleContacts: ArrayList<Contact> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        sampleContacts.clear()
        for (x in 1..3) {
            val contact = Contact("name$x", "+$x-555-555-5555")
            sampleContacts.add(contact)
            dLog(TAG, formatObject(contact.toString()))
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        mAdapter = ContactsAdapter(this, sampleContacts as List<Contact>, this)
        recyclerView = recycler_view
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = mAdapter
        recyclerView.setHasFixedSize(true)
        dLog(TAG, "Adapter Attached")
    }
}
