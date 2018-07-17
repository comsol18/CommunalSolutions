package com.communal_solutions.www.communalsolutions

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.communal_solutions.www.communalsolutions.Adapters.ContactsAdapter
import com.communal_solutions.www.communalsolutions.Adapters.formatObject
import com.communal_solutions.www.communalsolutions.HelperFiles.*
import kotlinx.android.synthetic.main.activity_contacts.*
import java.util.*

class ContactsActivity : AppCompatActivity() {

    private val TAG = "ContactsActivity"
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: ContactsAdapter
    private var sampleContacts: ArrayList<Contact> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        sampleContacts.clear()
        dLog(TAG, "Creating Sample Contacts")
        for (x in 0..3) {
            val number = "+$x-555-555-5555"
            val contact = Contact("Name $x", number)
            sampleContacts.add(contact)
            dLog(TAG, formatObject(contact.toString()))
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        dLog(TAG, "Initializing RecyclerView")
        recyclerView = recycler_view
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        mAdapter = ContactsAdapter(this, sampleContacts as List<Contact>, this)
        dLog(TAG, "mAdapter Created = $mAdapter")
        recyclerView.adapter = mAdapter
        dLog(TAG, "Adapter Attached = ${recyclerView.adapter != null}")
        recyclerView.setHasFixedSize(true)
    }
}
