package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter

interface ListContactUseCase {

    suspend fun allContactWithFilters() : List<ContactWithFilter>

    suspend fun getFilteredContactList(contactList: List<ContactWithFilter>, searchQuery: String, filterIndexes: ArrayList<Int>): List<ContactWithFilter>
}