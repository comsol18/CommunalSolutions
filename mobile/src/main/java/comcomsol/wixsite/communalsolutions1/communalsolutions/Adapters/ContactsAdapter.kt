package comcomsol.wixsite.communalsolutions1.communalsolutions.Adapters

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import comcomsol.wixsite.communalsolutions1.communalsolutions.HelperFiles.*
import comcomsol.wixsite.communalsolutions1.communalsolutions.R

class ContactsAdapter(private val context: Context, private val contactList: List<Contact>, private val parentActivity: Activity) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    private val TAG = "ContactsAdapter"

    init {
        dLog(TAG, "ContactsAdapter Successfully Instantiated.")
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val view: View? = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.contact_row_item, parent, false)
        val viewHolder: ViewHolder? = if (view == null) null else ViewHolder(view)
        dLog(TAG, "OnCreateViewHolder Properties: viewIsNull=${view == null}, viewHolderIsNull=${viewHolder == null}")
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val contact = contactList[position]
        //dLog(TAG, "Properties in sample $position: name=${contact.cName}, number=${contact.cPhoneNum}, image=${contact.cPhoto}")
        dLog(TAG, "OnBindViewHolder Properties: viewHolderIsNull=${holder == null}")
        try {
            holder!!.contactName.text = contact.cName
            holder.contactPhone.text = contact.cPhoneNum
        } catch (e: NullPointerException) { Log.e(TAG, "Holder is null") }
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var contactName: TextView
        var contactPhone: TextView

        init {
            contactName = itemView!!.findViewById(R.id.name)
            contactPhone = itemView.findViewById(R.id.phone)
            dLog(TAG, formatObject(toString()))
        }

        override fun toString(): String {
            return "ViewHolder(contactName=$contactName, contactPhone=$contactPhone)"
        }
    }
}