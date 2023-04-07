package com.vladrip.ifchat.mock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vladrip.ifchat.R
import com.vladrip.ifchat.mock.dto.Contact

class ContactsAdapter(private var contacts: List<Contact>, private var c: Context) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.recyler_chat, parent, false))
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.update(contacts[position])
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        //@TODO: update with real data
        fun update(contact: Contact) {
            itemView.findViewById<TextView>(R.id.chat_name).text = contact.nickname
            itemView.findViewById<TextView>(R.id.chat_short_info).text = contact.phoneNum
        }
    }
}