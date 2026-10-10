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

        if (json.isNull_or_empty_helper()) {
            val initial = getSampleContacts()
            saveContacts(initial)
            return initial.toMutableList()
        }

        val type = object : TypeToken<MutableList<Contact>>() {}.type

        return try {
            val list: MutableList<Contact>? = gson.fromJson(json, type)
            if (list.isNullOrEmpty()) {
                val initial = getSampleContacts()
                saveContacts(initial)
                initial.toMutableList()
            } else {
                list
            }
        } catch (e: Exception) {
            getSampleContacts().toMutableList()
        }
    }

    private fun String?.isNull_or_empty_helper(): Boolean {
        return this == null || this.trim().isEmpty()
    }

    private fun getSampleContacts(): List<Contact> {
        return listOf(
            Contact(1L, "An Nguyễn", "+84 90 123 4567", "an.nguyen@example.com", "Gia đình", false),
            Contact(2L, "Anh Thư", "Đại học Quốc Gia", "thu@dhqg.edu.vn", "Bạn bè", false),
            Contact(3L, "Bảo Trâm", "+84 98 765 4321", "tram@example.com", "Công việc", false),
            Contact(4L, "Bích Phương", "Phòng Kế toán", "phuong@ke-toan.com", "Công việc", false),
            Contact(5L, "Duy Khánh", "+84 91 223 3445", "khanh@example.com", "Bạn bè", false),
            Contact(6L, "Đức Huy", "Tech Lead", "huy@techlead.io", "Công việc", false),
            Contact(7L, "Hải Yến", "+84 93 456 7890", "yen@example.com", "Bạn bè", true),
            Contact(8L, "Hoàng Nam", "+84 94 567 8901", "nam.hoang@example.com", "Gia đình", true),
            Contact(9L, "Huy Trần", "+84 97 889 9001", "huytran@example.com", "Khác", false),
            Contact(10L, "Linh Chi", "+84 98 901 2345", "linhchi@example.com", "Bạn bè", true),
            Contact(11L, "Mai Linh", "+84 90 998 8776", "mailinh@example.com", "Gia đình", false),
            Contact(12L, "Mẹ", "+84 90 888 9999", "me@family.com", "Gia đình", true),
            Contact(13L, "Minh Quân", "+84 94 332 2110", "quan@example.com", "Công việc", false)
        )
    }
}
