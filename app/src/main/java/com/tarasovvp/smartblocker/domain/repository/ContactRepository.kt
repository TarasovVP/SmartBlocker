package com.tarasovvp.smartblocker.domain.repository

import android.content.Context
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact

interface ContactRepository {
    suspend fun getSystemContactList(context: Context, result: (Int, Int) -> Unit): ArrayList<Contact>

    suspend fun insertAllContacts(contactList: List<Contact>)

    suspend fun allContactWithFilters(): List<ContactWithFilter>

    suspend fun allContactsWithFiltersByFilter(filter: String): List<ContactWithFilter>
}