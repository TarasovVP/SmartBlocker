package com.tarasovvp.smartblocker.domain.usecase

import com.tarasovvp.smartblocker.data.database.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import javax.inject.Inject

class ListContactUseCase @Inject constructor(private val contactRepository: ContactRepository)  {

    suspend fun getContactsWithFilters() = contactRepository.getContactsWithFilters()

    suspend fun getHashMapFromContactList(contactList: List<ContactWithFilter>) = contactRepository.getHashMapFromContactList(contactList)
}