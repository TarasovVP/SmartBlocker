package com.tarasovvp.smartblocker.domain.repository

import android.content.Context
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Contact

interface ContactRepository {
    suspend fun getSystemContactList(context: Context, result: (Int, Int) -> Unit): ArrayList<Contact>

    suspend fun insertAllContacts(contactList: List<Contact>)

    suspend fun getContactsWithFilters(): List<ContactWithFilter>

    suspend fun getContactsWithFilterByFilter(filter: String): List<ContactWithFilter>
}