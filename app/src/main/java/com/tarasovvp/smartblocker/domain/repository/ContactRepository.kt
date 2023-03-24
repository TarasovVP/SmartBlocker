package com.tarasovvp.smartblocker.domain.repository

import android.content.Context
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Contact
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.NumberData

interface ContactRepository {
    suspend fun getSystemContactList(context: Context, result: (Int, Int) -> Unit): ArrayList<Contact>

    suspend fun setFilterToContact(filterList: List<Filter>, contactList: List<Contact>, result: (Int, Int) -> Unit): List<Contact>

    suspend fun insertContacts(contactList: List<Contact>)

    suspend fun getContactsWithFilters(): List<ContactWithFilter>

    suspend fun getContactsWithFilterByFilter(filter: String): List<ContactWithFilter>

    suspend fun getHashMapFromContactList(contactList: List<ContactWithFilter>): Map<String, List<ContactWithFilter>>

    suspend fun filteredNumberDataList(filter: Filter?, numberDataList: ArrayList<NumberData>, color: Int): ArrayList<NumberData>
}