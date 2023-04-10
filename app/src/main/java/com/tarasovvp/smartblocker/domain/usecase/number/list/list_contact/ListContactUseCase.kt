package com.tarasovvp.smartblocker.domain.usecase.number.list.list_contact

import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter

interface ListContactUseCase {

    suspend fun getContactsWithFilters() : List<ContactWithFilter>

    suspend fun getFilteredContactList(contactList: List<ContactWithFilter>, searchQuery: String, filterIndexes: ArrayList<Int>): List<ContactWithFilter>

    suspend fun getHashMapFromContactList(contactList: List<ContactWithFilter>): Map<String, List<ContactWithFilter>>
}