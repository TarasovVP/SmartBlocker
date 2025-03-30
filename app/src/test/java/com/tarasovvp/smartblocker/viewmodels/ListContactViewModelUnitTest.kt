package com.tarasovvp.smartblocker.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NAME
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.entities.dbentities.Contact
import com.tarasovvp.smartblocker.domain.entities.dbviews.ContactWithFilter
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.usecases.ListContactUseCase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.presentation.main.number.list.listcontact.ListContactViewModel
import com.tarasovvp.smartblocker.presentation.mappers.ContactWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.uimodels.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class ListContactViewModelUnitTest : BaseViewModelUnitTest<ListContactViewModel>() {
    @MockK
    private lateinit var useCase: ListContactUseCase

    @MockK
    private lateinit var contactWithFilterUIMapper: ContactWithFilterUIMapper

    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    override fun createViewModel() = ListContactViewModel(application, useCase, contactWithFilterUIMapper, savedStateHandle)

    @Test
    fun getContactsWithFiltersTest() =
        runTest {
            val contactList =
                listOf(
                    ContactWithFilter(contact = Contact(name = TEST_NAME)),
                    ContactWithFilter(contact = Contact(name = TEST_NUMBER)),
                )
            val contactUIModelList =
                listOf(
                    ContactWithFilterUIModel(contactName = TEST_NAME),
                    ContactWithFilterUIModel(contactName = TEST_NUMBER),
                )
            coEvery { useCase.allContactWithFilters() } returns contactList
            every { contactWithFilterUIMapper.mapToUIModelList(contactList) } returns contactUIModelList
            viewModel.getContactsWithFilters(false)
            advanceUntilIdle()
            coVerify { useCase.allContactWithFilters() }
            verify { contactWithFilterUIMapper.mapToUIModelList(contactList) }
            assertEquals(contactUIModelList, viewModel.contactListLiveData.getOrAwaitValue())
        }

    @Test
    fun getFilteredContactListTest() =
        runTest {
            val contactList =
                listOf(
                    ContactWithFilterUIModel(
                        contactName = TEST_NAME,
                        filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(filterType = Constants.BLOCKER),
                    ),
                    ContactWithFilterUIModel(contactName = "zxy"),
                )
            val searchQuery = String.EMPTY
            val filterIndexes = arrayListOf(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal)
            val expectedContactList =
                contactList.filter { it.filterWithFilteredNumberUIModel.filterType == Constants.BLOCKER }
            viewModel.getFilteredContactList(contactList, searchQuery, filterIndexes)
            advanceUntilIdle()
            assertEquals(expectedContactList, viewModel.filteredContactListLiveData.getOrAwaitValue())
        }
}
