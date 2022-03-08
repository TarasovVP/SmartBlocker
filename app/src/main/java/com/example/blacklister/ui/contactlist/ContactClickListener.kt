package com.example.blacklister.ui.contactlist

import com.example.blacklister.model.Contact

interface ContactClickListener {
    fun onContactClicked(contact: Contact)
}