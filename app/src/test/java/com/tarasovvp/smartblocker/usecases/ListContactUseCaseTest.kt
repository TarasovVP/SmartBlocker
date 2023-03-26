package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_contact.ListContactUseCaseImpl
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

//TODO unfinished
@Suppress
@RunWith(MockitoJUnitRunner::class)
class ListContactUseCaseTest {

    @Mock
    private lateinit var contactRepository: ContactRepository

    private lateinit var listContactUseCaseImpl: ListContactUseCaseImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        listContactUseCaseImpl = ListContactUseCaseImpl(contactRepository)
    }

    suspend fun getContactsWithFilters() = contactRepository.getContactsWithFilters()

    suspend fun getHashMapFromContactList(contactList: List<ContactWithFilter>) = contactRepository.getHashMapFromContactList(contactList)
}