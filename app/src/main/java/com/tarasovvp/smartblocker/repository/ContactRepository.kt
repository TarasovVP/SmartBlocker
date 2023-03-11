package com.tarasovvp.smartblocker.repository

import android.content.Context
import com.tarasovvp.smartblocker.models.*

interface ContactRepository {
    suspend fun getSystemContactList(context: Context, filterRepository: FilterRepository, result: (Int, Int) -> Unit): ArrayList<Contact>

    suspend fun insertContacts(list: List<Contact>)

    suspend fun getContactsWithFilters(): List<ContactWithFilter>

    suspend fun getContactsWithFilterByFilter(filter: String): List<ContactWithFilter>

    suspend fun getHashMapFromContactList(contactList: List<ContactWithFilter>): Map<String, List<ContactWithFilter>>

    suspend fun filteredNumberDataList(filter: Filter?, numberDataList: ArrayList<NumberData>, color: Int): ArrayList<NumberData>
}