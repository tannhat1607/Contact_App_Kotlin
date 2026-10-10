package com.example.contactapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoriteAdapter(
    private var favoriteContacts: List<Contact>,
    private val onAddNewClick: () -> Unit,
    private val onContactClick: (Contact) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private val colors = listOf(
        "#3B82F6", "#8B5CF6", "#EC4899", "#F59E0B",
        "#10B981", "#06B6D4", "#6366F1", "#14B8A6"
    )

    class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: TextView = view.findViewById(R.id.txtFavAvatar)
        val name: TextView = view.findViewById(R.id.txtFavName)
        val badge: ImageView = view.findViewById(R.id.imgFavBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        if (position == 0) {
            holder.avatar.text = "+"
            holder.avatar.textSize = 28f
            holder.avatar.setTextColor(Color.parseColor("#1D4ED8"))
            holder.avatar.background.setTint(Color.parseColor("#E2E8F0"))
            holder.name.text = "Thêm mới"
            holder.badge.visibility = View.GONE

            holder.itemView.setOnClickListener { onAddNewClick() }
        } else {
            val contact = favoriteContacts[position - 1]
            val initial = contact.name.trim().firstOrNull()?.uppercase() ?: "?"

            holder.avatar.text = initial
            holder.avatar.textSize = 22f
            holder.avatar.setTextColor(Color.WHITE)

            val colorIndex = Math.abs(contact.name.hashCode()) % colors.size
            holder.avatar.background.setTint(Color.parseColor(colors[colorIndex]))

            holder.name.text = contact.name
            holder.badge.visibility = View.VISIBLE

            holder.itemView.setOnClickListener { onContactClick(contact) }
        }
    }

    override fun getItemCount(): Int {
        return favoriteContacts.size + 1
    }

    fun updateData(newFavorites: List<Contact>) {
        favoriteContacts = newFavorites
        notifyDataSetChanged()
    }
}
