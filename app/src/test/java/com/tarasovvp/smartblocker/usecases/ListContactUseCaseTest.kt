package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class ListContactUseCaseTest @Inject constructor(private val contactRepository: ContactRepository){

    suspend fun getContactsWithFilters() = contactRepository.getContactsWithFilters()

    suspend fun getHashMapFromContactList(contactList: List<ContactWithFilter>) = contactRepository.getHashMapFromContactList(contactList)
}