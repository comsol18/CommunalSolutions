package com.communal_solutions.www.communalsolutions.Adapters

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.communal_solutions.www.communalsolutions.HelperFiles.*
import com.communal_solutions.www.communalsolutions.R
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.squareup.picasso.Picasso

class ContactsAdapter(private val contactList: List<Contact>) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>(), Filterable {
    private val TAG = "ContactsAdapter"
    private var contactListFiltered: List<Contact>

    init {
        contactListFiltered = contactList
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = contactList
                } else {
                    val filteredList = ArrayList<Contact>()
                    for (row in contactList) {
                        if (row.cName.toLowerCase().contains(charString.toLowerCase()) || row.cPhoneNum.contains(charSequence)) {
                            filteredList.add(row)
                        }
                    }
                    contactListFiltered = filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = contactListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                contactListFiltered = filterResults.values as ArrayList<Contact>
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val view: View? = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.contact_row_item, parent, false)
        return if (view == null) null else ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val contact = contactListFiltered[position]
        try {
            holder!!.contactName.text = contact.cName
            holder.contactPhone.text = contact.cPhoneNum
            val firstLetter = contact.cName[0].toUpperCase().toString()
            val color = ColorGenerator.MATERIAL.randomColor
            val drawable = TextDrawable.builder()
                    .buildRound(firstLetter,color)
            holder.contactImg.setImageDrawable(drawable)
        } catch (e: NullPointerException) { Log.e(TAG, "Holder is null") }
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var contactImg: ImageView
        var contactName: TextView
        var contactPhone: TextView

        init {
            contactImg = itemView!!.findViewById(R.id.thumbnail)
            contactName = itemView.findViewById(R.id.name)
            contactPhone = itemView.findViewById(R.id.phone)
            // dLog(TAG, formatObject(toString()))
        }

        override fun toString(): String {
            return "ViewHolder(contactName=$contactName, contactPhone=$contactPhone)"
        }
    }
}