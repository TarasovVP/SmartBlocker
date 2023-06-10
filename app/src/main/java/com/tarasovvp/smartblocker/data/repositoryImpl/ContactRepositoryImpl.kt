package com.tarasovvp.smartblocker.data.repositoryImpl

import android.content.Context
import com.tarasovvp.smartblocker.data.database.dao.ContactDao
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.utils.AppPhoneNumberUtil
import com.tarasovvp.smartblocker.utils.extensions.systemContactList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val appPhoneNumberUtil: AppPhoneNumberUtil,
    private val contactDao: ContactDao
) : ContactRepository {

    override suspend fun getSystemContactList(
        context: Context,
        country: String,
        result: (Int, Int) -> Unit,
    ) =
        withContext(Dispatchers.Default) {
            context.systemContactList(appPhoneNumberUtil, country) { size, position ->
                result.invoke(size, position)
            }
        }

    override suspend fun insertAllContacts(contactList: List<Contact>) =
        contactDao.insertAllContacts(contactList)

    override suspend fun allContactWithFilters() =
        contactDao.allContactsWithFilters()

    override suspend fun allContactsWithFiltersByFilter(filter: String) =
        contactDao.allContactsWithFiltersByFilter(filter)

    override suspend fun allContactsWithFiltersByCreateFilter(filter: String) =
        contactDao.allContactsWithFiltersByCreateFilter(filter)
}