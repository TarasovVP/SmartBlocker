package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.toHashMapFromList
import com.tarasovvp.blacklister.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ContactRepository {

    private val dao = BlackListerApp.instance?.database?.contactDao()

    suspend fun insertContacts(list: List<Contact>) {
        dao?.insertAllContacts(list)
    }

    fun getAllContacts(): List<Contact>? {
        return dao?.getAllContacts()
    }

    fun getContactByNumber(phone: String): Contact? {
        return dao?.getContactByPhone(phone)
    }

    fun updateContact(contact: Contact) {
        dao?.updateContact(contact)
    }

    suspend fun getHashMapFromContactList(contactList: List<Contact>): HashMap<String, List<Contact>> =
        withContext(
            Dispatchers.Default
        ) {
            contactList.toHashMapFromList()
        }
}