package com.example.contactapp


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private var contacts: List<Contact>,
    private val onClick: (Contact) -> Unit,
    private val onFavoriteClick: (Contact) -> Unit,
    private val onLongClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val avatar: TextView =
            view.findViewById(R.id.txtAvatar)

        val name: TextView =
            view.findViewById(R.id.txtName)

        val phone: TextView =
            view.findViewById(R.id.txtPhone)

        val favorite: TextView =
            view.findViewById(R.id.txtFavorite)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)

        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ContactViewHolder,
        position: Int
    ) {
        val contact = contacts[position]

        holder.name.text = contact.name
        holder.phone.text = contact.phone

        holder.avatar.text = contact.name
            .trim()
            .firstOrNull()
            ?.uppercase() ?: "?"

        holder.favorite.text =
            if (contact.isFavorite) "★" else "☆"

        holder.itemView.setOnClickListener {
            onClick(contact)
        }

        holder.favorite.setOnClickListener {
            onFavoriteClick(contact)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(contact)
            true
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun updateData(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }
}
