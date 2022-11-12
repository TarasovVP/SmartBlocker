package com.tarasovvp.blacklister.repository

import android.content.Context
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.orZero
import com.tarasovvp.blacklister.extensions.systemContactList
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ContactRepository {

    private val contactDao = BlackListerApp.instance?.database?.contactDao()

    suspend fun insertContacts(list: List<Contact>) {
        contactDao?.insertAllContacts(list)
    }

    suspend fun getAllContacts(): List<Contact>? =
        withContext(
            Dispatchers.Default
        ) {
            contactDao?.getAllContacts()
        }

    suspend fun getQueryContactList(filter: Filter): List<Contact>? {
        return contactDao?.queryContactList(if (filter.isPreview) filter.addFilter() else filter.filter,
            filter.conditionType)
            ?.filter {
                it.filter?.filter?.length.orZero() < (if (filter.isPreview) filter.addFilter() else filter.filter).length
                        && it.trimmedPhone.indexOf(it.filter?.filter.orEmpty()) < it.trimmedPhone.indexOf(filter.filter)
            }
    }

    suspend fun getSystemContactList(context: Context): ArrayList<Contact> =
        withContext(
            Dispatchers.Default
        ) {
            context.systemContactList()
        }

    suspend fun getHashMapFromContactList(contactList: List<Contact>): Map<String, List<Contact>> =
        withContext(
            Dispatchers.Default
        ) {
            contactList.groupBy {
                if (it.name.isNullOrEmpty()) String.EMPTY else it.name?.get(0).toString()
            }
        }
}