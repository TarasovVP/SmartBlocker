package com.tarasovvp.blacklister.repository

import android.content.Context
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.contactList
import com.tarasovvp.blacklister.extensions.toHashMapFromList
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ContactRepository {

    private val dao = BlackListerApp.instance?.database?.contactDao()

    fun insertContacts(list: List<Contact>) {
        dao?.insertAllContacts(list)
    }

    suspend fun getAllContacts(): List<Contact>? =
        withContext(
            Dispatchers.Default
        ) {
            dao?.getAllContacts()
        }

    fun getQueryContacts(filter: Filter): List<Contact>? {
        return dao?.queryContactList(filter.filter, filter.contain, filter.start, filter.end)
    }

    fun getContactByPhone(phone: String): Contact? {
        return dao?.getContactByPhone(phone)
    }

    fun updateContact(contact: Contact) {
        dao?.updateContact(contact)
    }

    suspend fun getSystemContactList(context: Context): ArrayList<Contact> =
        withContext(
            Dispatchers.Default
        ) {
            context.contactList()
        }

    suspend fun getHashMapFromContactList(contactList: List<Contact>): HashMap<String, List<Contact>> =
        withContext(
            Dispatchers.Default
        ) {
            contactList.toHashMapFromList()
        }
}