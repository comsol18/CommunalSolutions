package com.communal_solutions.www.communalsolutions

import android.app.Activity
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
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
    private var contacts: ArrayList<Contact> = ArrayList()

    private fun initList() {
        val cursor: Cursor = this.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME
        )

        cursor.moveToFirst()
        contacts.add(Contact("dummy", "dummy"))

        while (cursor.moveToNext()) {
            contacts.add(
                Contact(
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        initList()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        // dLog(TAG, "Initializing RecyclerView")
        recyclerView = recycler_view
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        mAdapter = ContactsAdapter(this, contacts as List<Contact>, this)
        // dLog(TAG, "mAdapter Created = $mAdapter")
        recyclerView.adapter = mAdapter
        // dLog(TAG, "Adapter Attached = ${recyclerView.adapter != null}")
        recyclerView.setHasFixedSize(true)
    }
}
