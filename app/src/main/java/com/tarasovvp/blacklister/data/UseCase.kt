package com.tarasovvp.blacklister.data

import android.content.Context
import com.tarasovvp.blacklister.extensions.contactList
import com.tarasovvp.blacklister.model.Contact

class UseCase {

    suspend fun getContactList(context: Context): List<Contact> {
        return context.contactList()
    }
}