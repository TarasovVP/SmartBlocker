package com.example.blacklister.provider

import com.example.blacklister.BlackListerApp
import com.example.blacklister.extensions.toHashMapFromList
import com.example.blacklister.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ContactRepository {
    suspend fun insertContacts(list: List<Contact>)
    fun getAllContacts(): List<Contact>?
    fun updateContact(contact: Contact)
    suspend fun getHashMapFromContactList(contactList: List<Contact>): HashMap<String, List<Contact>>
}

object ContactRepositoryImpl : ContactRepository {

    private val dao = BlackListerApp.instance?.database?.contactDao()

    override suspend fun insertContacts(list: List<Contact>) {
        dao?.insertAllContacts(list)
    }

    override fun getAllContacts(): List<Contact>? {
        return dao?.getAllContacts()
    }

    override fun updateContact(contact: Contact) {
        dao?.updateContact(contact)
    }

    override suspend fun getHashMapFromContactList(contactList: List<Contact>): HashMap<String, List<Contact>> =
        withContext(
            Dispatchers.Default
        ) {
            contactList.toHashMapFromList()
        }
}