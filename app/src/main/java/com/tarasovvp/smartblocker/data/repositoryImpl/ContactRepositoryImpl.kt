package com.tarasovvp.smartblocker.data.repositoryImpl

import android.content.Context
import com.tarasovvp.smartblocker.data.database.dao.ContactDao
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.utils.PhoneNumber
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val phoneNumber: PhoneNumber,
    private val contactDao: ContactDao,
) : ContactRepository {

    override suspend fun getSystemContactList(context: Context, result: (Int, Int) -> Unit, ): ArrayList<Contact> =
        withContext(Dispatchers.Default) {
            context.systemContactList(phoneNumber) { size, position ->
                result.invoke(size, position)
            }
        }

    override suspend fun insertAllContacts(contactList: List<Contact>) =
        contactDao.insertAllContacts(contactList)

    override suspend fun allContactWithFilters(): List<ContactWithFilter> =
        contactDao.allContactsWithFilters()

    override suspend fun allContactsWithFiltersByFilter(filter: String) =
        contactDao.allContactsWithFiltersByFilter(filter)
}