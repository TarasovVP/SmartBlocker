package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NAME
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.domain.usecases.ListContactUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_contact.ListContactUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ListContactUseCaseUnitTest {

    @MockK
    private lateinit var contactRepository: ContactRepository

    private lateinit var listContactUseCase: ListContactUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        listContactUseCase = ListContactUseCaseImpl(contactRepository)
    }

    @Test
    fun allContactWithFiltersTest() = runBlocking {
        val contactList = listOf(ContactWithFilter(contact = Contact(name = TEST_NAME)))
        coEvery { contactRepository.allContactWithFilters() } returns contactList
        val result = listContactUseCase.allContactWithFilters()
        assertEquals(contactList, result)
    }
}