package com.example.contactapp


data class Contact (
    val id: Long = 0L,
    var name: String,
    var phone: String,
    var email: String = "",
    var groupName: String = "Khác",
    var isFavorite: Boolean = false
)