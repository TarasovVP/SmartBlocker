package com.tarasovvp.smartblocker.data.repositoryImpl

import android.content.Context
import com.tarasovvp.smartblocker.data.database.dao.ContactDao
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Contact
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao
) : ContactRepository {

    override suspend fun insertContacts(contactList: List<Contact>) {
        contactDao.insertAllContacts(contactList)
    }

    override suspend fun setFilterToContact(filterList: List<Filter>, contactList: List<Contact>, result: (Int, Int) -> Unit): List<Contact> =
        withContext(
            Dispatchers.Default
        ) {
            contactList.onEachIndexed { index, contact ->
                val filter = filterList.filter { filter ->
                    (contact.phoneNumberValue() == filter.filter && filter.isTypeFull())
                            || (contact.phoneNumberValue().startsWith(filter.filter) && filter.isTypeStart())
                            || (contact.phoneNumberValue().contains(filter.filter) && filter.isTypeContain())
                }.sortedWith(compareByDescending<Filter> { it.filter.length }.thenBy { contact.phoneNumberValue().indexOf(it.filter) }).firstOrNull()
                contact.filter = filter?.filter.orEmpty()
                filter?.filteredContacts = filter?.filteredContacts.orZero() + 1
                result.invoke(contactList.size, index)
            }
        }

    override suspend fun getContactsWithFilters(): List<ContactWithFilter> = contactDao.getContactsWithFilters()

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
    ): ArrayList<NumberData> = withContext(Dispatchers.Default) {
        val filteredList = arrayListOf<NumberData>()
        val supposedFilteredList = arrayListOf<NumberData>()
        numberDataList.forEach { numberData ->
            numberData.highlightedSpanned = numberData.highlightedSpanned(filter, color)
            if (numberData is ContactWithFilter && numberData.contact?.number?.startsWith(Constants.PLUS_CHAR).isTrue().not()) {
                supposedFilteredList.add(numberData)
            } else {
                filteredList.add(numberData)
            }
        }
        filteredList.addAll(supposedFilteredList)
        filteredList
        }
}