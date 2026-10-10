package com.example.contactapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ContactStorage(context: Context) {

    private val prefs = context.getSharedPreferences(
        "contact_preferences",
        Context.MODE_PRIVATE
    )

    private val gson = Gson()

    fun saveContacts(contacts: List<Contact>) {
        val json = gson.toJson(contacts)

        prefs.edit()
            .putString("contacts", json)
            .apply()
    }

    fun loadContacts(): MutableList<Contact> {
        val json = prefs.getString("contacts", null)
            ?: return mutableListOf()

        val type = object : TypeToken<MutableList<Contact>>() {}.type

        return try {
            gson.fromJson<MutableList<Contact>>(json, type)
                ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }
}
