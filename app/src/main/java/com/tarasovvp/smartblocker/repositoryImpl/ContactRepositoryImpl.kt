package com.tarasovvp.smartblocker.repositoryImpl

import android.content.Context
import com.tarasovvp.smartblocker.database.dao.ContactDao
import com.tarasovvp.smartblocker.database.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.database.entities.Contact
import com.tarasovvp.smartblocker.database.entities.Filter
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.extensions.filteredNumberDataList
import com.tarasovvp.smartblocker.extensions.orZero
import com.tarasovvp.smartblocker.extensions.systemContactList
import com.tarasovvp.smartblocker.models.*
import com.tarasovvp.smartblocker.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactDao:  ContactDao
) : ContactRepository {

    override suspend fun insertContacts(contactList: List<Contact>) {
        contactDao.insertAllContacts(contactList)
    }

    override suspend fun setFilterToContact(filterList: ArrayList<Filter>?, contactList: List<Contact>, result: (Int, Int) -> Unit): List<Contact> =
        withContext(
            Dispatchers.Default
        ) {
            contactList.onEachIndexed { index, contact ->
                val filter = filterList?.filter { filter ->
                    (contact.phoneNumberValue() == filter.filter && filter.isTypeFull())
                            || (contact.phoneNumberValue().startsWith(filter.filter) && filter.isTypeStart())
                            || (contact.phoneNumberValue().contains(filter.filter) && filter.isTypeContain())
                }?.sortedWith(compareByDescending<Filter> { it.filter.length }.thenBy { contact.phoneNumberValue().indexOf(it.filter) })?.firstOrNull()
                contact.filter = filter?.filter.orEmpty()
                filter?.filteredContacts = filter?.filteredContacts.orZero() + 1
                result.invoke(contactList.size, index)
            }
        }

    override suspend fun getContactsWithFilters(): List<ContactWithFilter> =
        withContext(
            Dispatchers.Default
        ) {
            contactDao.getContactsWithFilters()
        }

    override suspend fun getContactsWithFilterByFilter(filter: String) = contactDao.getContactsWithFiltersByFilter(filter)

    override suspend fun getSystemContactList(
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