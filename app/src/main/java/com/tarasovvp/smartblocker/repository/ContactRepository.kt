package com.tarasovvp.smartblocker.repository

import android.content.Context
import com.tarasovvp.smartblocker.BlackListerApp
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.extensions.filteredNumberDataList
import com.tarasovvp.smartblocker.extensions.systemContactList
import com.tarasovvp.smartblocker.models.Contact
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.NumberData
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


    suspend fun getSystemContactList(
        context: Context,
        result: (Int, Int) -> Unit,
    ): ArrayList<Contact> =
        withContext(
            Dispatchers.Default
        ) {
            context.systemContactList { size, position ->
                result.invoke(size, position)
            }
        }

    suspend fun getHashMapFromContactList(contactList: List<Contact>): Map<String, List<Contact>> =
        withContext(
            Dispatchers.Default
        ) {
            contactList.groupBy {
                if (it.name.isNullOrEmpty()) String.EMPTY else it.name?.get(0).toString()
            }
        }

    suspend fun filteredNumberDataList(
        filter: Filter?,
        numberDataList: ArrayList<NumberData>,
    ): ArrayList<NumberData> =
        withContext(
            Dispatchers.Default
        ) {
            numberDataList.filteredNumberDataList(filter)
        }
}