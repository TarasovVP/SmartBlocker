package com.tarasovvp.smartblocker.domain.usecase.number.list.list_contact

import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import javax.inject.Inject

class ListContactUseCaseImpl @Inject constructor(private val contactRepository: ContactRepository):
    ListContactUseCase {

    override suspend fun getContactsWithFilters() = contactRepository.getContactsWithFilters()

    override suspend fun getHashMapFromContactList(contactList: List<ContactWithFilter>) = contactRepository.getHashMapFromContactList(contactList)
}