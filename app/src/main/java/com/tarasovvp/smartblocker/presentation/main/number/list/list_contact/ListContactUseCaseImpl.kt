package com.tarasovvp.smartblocker.presentation.main.number.list.list_contact

import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.domain.usecase.ListContactUseCase
import javax.inject.Inject

class ListContactUseCaseImpl @Inject constructor(private val contactRepository: ContactRepository):
    ListContactUseCase {

    override suspend fun getContactsWithFilters() = contactRepository.getContactsWithFilters()

    override suspend fun getFilteredContactList(contactList: List<ContactWithFilter>, searchQuery: String, filterIndexes: ArrayList<Int>) = contactRepository.getFilteredContactList(contactList, searchQuery, filterIndexes)

    override suspend fun getHashMapFromContactList(contactList: List<ContactWithFilter>) = contactRepository.getHashMapFromContactList(contactList)
}