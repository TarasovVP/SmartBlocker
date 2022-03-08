package com.example.blacklister.provider

import androidx.lifecycle.LiveData
import com.example.blacklister.model.Contact
import com.example.blacklister.ui.BlackListerApp

interface ContactRepository {
    suspend fun inasertContacts(list: List<Contact>)
    fun subscribeToContacts(): LiveData<List<Contact>>?
}

object ContactRepositoryImpl : ContactRepository {

    private val dao = BlackListerApp.instance?.database?.contactDao()

    override suspend fun inasertContacts(list: List<Contact>) {
        dao?.insertAllContacts(list)
    }

    override fun subscribeToContacts(): LiveData<List<Contact>>? {
        return dao?.getAllContacts()
    }


}