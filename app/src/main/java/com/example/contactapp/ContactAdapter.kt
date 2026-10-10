package com.example.contactapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

sealed class ContactListItem {
    data class Header(val letter: String) : ContactListItem()
    data class ContactItem(val contact: Contact) : ContactListItem()
}

class ContactAdapter(
    private var items: List<ContactListItem> = emptyList(),
    private val onClick: (Contact) -> Unit,
    private val onCallClick: (Contact) -> Unit,
    private val onLongClick: (Contact) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CONTACT = 1
    }

    private val colors = listOf(
        "#2563EB", "#0284C7", "#0D9488", "#059669",
        "#7C3AED", "#C026D3", "#D97706", "#DC2626"
    )

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val letter: TextView = view.findViewById(R.id.txtHeaderLetter)
    }

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: TextView = view.findViewById(R.id.txtAvatar)
        val name: TextView = view.findViewById(R.id.txtName)
        val phone: TextView = view.findViewById(R.id.txtPhone)
        val btnCall: ImageView = view.findViewById(R.id.btnCallItem)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ContactListItem.Header -> TYPE_HEADER
            is ContactListItem.ContactItem -> TYPE_CONTACT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            val view = inflater.inflate(R.layout.item_section_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_contact, parent, false)
            ContactViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ContactListItem.Header -> {
                (holder as HeaderViewHolder).letter.text = item.letter
            }
            is ContactListItem.ContactItem -> {
                val contactHolder = holder as ContactViewHolder
                val contact = item.contact

                contactHolder.name.text = contact.name
                
                val subtitle = if (contact.phone.isNotBlank()) {
                    contact.phone
                } else if (contact.groupName.isNotBlank()) {
                    contact.groupName
                } else {
                    contact.email
                }
                contactHolder.phone.text = subtitle

                val initial = contact.name.trim().firstOrNull()?.uppercase() ?: "?"
                contactHolder.avatar.text = initial

                val colorIndex = Math.abs(contact.name.hashCode()) % colors.size
                contactHolder.avatar.background.setTint(Color.parseColor(colors[colorIndex]))

                contactHolder.btnCall.setOnClickListener {
                    onCallClick(contact)
                }

                contactHolder.itemView.setOnClickListener {
                    onClick(contact)
                }

                contactHolder.itemView.setOnLongClickListener {
                    onLongClick(contact)
                    true
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(contacts: List<Contact>, isAscending: Boolean = true) {
        items = buildDisplayItems(contacts, isAscending)
        notifyDataSetChanged()
    }

    private fun buildDisplayItems(contacts: List<Contact>, isAscending: Boolean): List<ContactListItem> {
        val result = mutableListOf<ContactListItem>()
        val sorted = if (isAscending) {
            contacts.sortedBy { it.name.lowercase() }
        } else {
            contacts.sortedByDescending { it.name.lowercase() }
        }

        var currentLetter = ""
        for (contact in sorted) {
            val firstLetter = contact.name.trim().firstOrNull()?.uppercase() ?: "#"
            if (firstLetter != currentLetter) {
                currentLetter = firstLetter
                result.add(ContactListItem.Header(currentLetter))
            }
            result.add(ContactListItem.ContactItem(contact))
        }
        return result
    }
}
