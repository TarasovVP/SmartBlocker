package com.example.blacklister.data

import android.content.Context
import com.example.blacklister.extensions.contactList
import com.example.blacklister.model.Contact

class UseCase {

    suspend fun getContactList(context: Context): List<Contact> {
        return context.contactList()
    }
}