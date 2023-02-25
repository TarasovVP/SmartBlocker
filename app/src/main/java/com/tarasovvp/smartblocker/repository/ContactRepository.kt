package com.tarasovvp.smartblocker.repository

import android.content.Context
import com.tarasovvp.smartblocker.models.Contact
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.NumberData

interface ContactRepository {
    suspend fun insertContacts(list: List<Contact>)

    suspend fun getAllContacts(): List<Contact>

    suspend fun getSystemContactList(context: Context, filterRepository: FilterRepository, result: (Int, Int) -> Unit): ArrayList<Contact>

    suspend fun getHashMapFromContactList(contactList: List<Contact>): Map<String, List<Contact>>

    suspend fun filteredNumberDataList(filter: Filter?, numberDataList: ArrayList<NumberData>, color: Int): ArrayList<NumberData>
}