package com.tarasovvp.smartblocker.domain.repository

import android.content.Context
import com.tarasovvp.smartblocker.domain.entities.dbentities.Contact
import com.tarasovvp.smartblocker.domain.entities.dbviews.ContactWithFilter

interface ContactRepository {
    suspend fun getSystemContactList(
        context: Context,
        country: String,
        result: (Int, Int) -> Unit,
    ): ArrayList<Contact>

    suspend fun insertAllContacts(contactList: List<Contact>)

    suspend fun allContactWithFilters(): List<ContactWithFilter>

    suspend fun allContactsWithFiltersByFilter(filter: String): List<ContactWithFilter>

    suspend fun allContactsWithFiltersByCreateFilter(filter: String): List<ContactWithFilter>
}
