package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter

interface ListContactUseCase {

    suspend fun getContactsWithFilters() : List<ContactWithFilter>

    suspend fun getFilteredContactList(contactList: List<ContactWithFilter>, searchQuery: String, filterIndexes: ArrayList<Int>): List<ContactWithFilter>
}