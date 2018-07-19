package com.communal_solutions.www.communalsolutions

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.communal_solutions.www.communalsolutions.Adapters.ContactsAdapter
import com.communal_solutions.www.communalsolutions.HelperFiles.*
import kotlinx.android.synthetic.main.activity_contacts.*
import java.util.*

class ContactsActivity : AppCompatActivity() {

    private val TAG = "ContactsActivity"
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: ContactsAdapter
    private var contacts: ArrayList<Contact> = ArrayList()
    private var searchView: SearchView? = null

    private fun initList() {
        val cursor: Cursor = this.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME.toLowerCase() + " ASC"
        )

        cursor.moveToFirst()
        contacts.add(Contact("I", "I"))

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.contact_search, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.action_search)
                .actionView as SearchView
        searchView!!.setSearchableInfo(searchManager
                .getSearchableInfo(componentName))
        searchView!!.maxWidth = Integer.MAX_VALUE

        // listening to search query text change
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                mAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                mAdapter.filter.filter(query)
                return false
            }
        })
        return true
    }

    private fun initRecyclerView() {
        recyclerView = recycler_view
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        mAdapter = ContactsAdapter(contacts as List<Contact>)
        recyclerView.adapter = mAdapter
        recyclerView.setHasFixedSize(true)
    }
}
