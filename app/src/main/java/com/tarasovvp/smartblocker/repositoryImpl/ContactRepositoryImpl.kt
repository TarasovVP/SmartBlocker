package com.tarasovvp.smartblocker.repositoryImpl

import android.content.Context
import com.tarasovvp.smartblocker.database.dao.ContactDao
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.extensions.filteredNumberDataList
import com.tarasovvp.smartblocker.extensions.systemContactList
import com.tarasovvp.smartblocker.models.Contact
import com.tarasovvp.smartblocker.models.ContactWithFilter
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.repository.ContactRepository
import com.tarasovvp.smartblocker.repository.FilterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactDao:  ContactDao
) : ContactRepository {
    override suspend fun insertContacts(list: List<Contact>) {
        contactDao.insertAllContacts(list)
    }

    override suspend fun getContactsWithFilters(): List<ContactWithFilter> =
        withContext(
            Dispatchers.Default
        ) {
            contactDao.getContactsWithFilters()
        }

    override suspend fun getSystemContactList(
        context: Context,
        filterRepository: FilterRepository,
        result: (Int, Int) -> Unit,
    ): ArrayList<Contact> =
        withContext(
            Dispatchers.Default
        ) {
            context.systemContactList(filterRepository) { size, position ->
                result.invoke(size, position)
            }
        }

    override suspend fun getHashMapFromContactList(contactList: List<ContactWithFilter>): Map<String, List<ContactWithFilter>> =
        withContext(
            Dispatchers.Default
        ) {
            contactList.groupBy {
                if (it.contact?.name.isNullOrEmpty()) String.EMPTY else it.contact?.name?.get(0).toString()
            }
        }

    override suspend fun filteredNumberDataList(
        filter: Filter?,
        numberDataList: ArrayList<NumberData>,
        color: Int
    ): ArrayList<NumberData> =
        withContext(
            Dispatchers.Default
        ) {
            numberDataList.filteredNumberDataList(filter, color)
        }
}