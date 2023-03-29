package com.tarasovvp.smartblocker.usecases

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.TestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecase.number.details.details_filter.DetailsFilterUseCase
import com.tarasovvp.smartblocker.domain.usecase.number.details.details_filter.DetailsFilterUseCaseImpl
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DetailsFilterUseCaseTest {

    @Mock
    private lateinit var contactRepository: ContactRepository

    @Mock
    private lateinit var filterRepository: FilterRepository

    @Mock
    private lateinit var logCallRepository: LogCallRepository

    @Mock
    private lateinit var filteredCallRepository: FilteredCallRepository

    private lateinit var detailsFilterUseCase: DetailsFilterUseCase

    private val resultMock = mock<() -> Unit>()

    @Test
    fun getQueryContactCallListTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        val callList = listOf(LogCallWithFilter().apply { call = LogCall().apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall().apply { number = "2"} })
        val contactList = listOf(ContactWithFilter(contact =  Contact(number = "1")), ContactWithFilter(contact =  Contact(number = "1")))
        val numberDataList = ArrayList<NumberData>().apply {
            addAll(callList)
            addAll(contactList)
        }.sortedBy {
            if (it is ContactWithFilter) it.contact?.number else if (it is LogCallWithFilter) it.call?.number else String.EMPTY
        }
        Mockito.`when`(logCallRepository.getLogCallWithFilterByFilter(filter.filter))
            .thenReturn(callList)
        Mockito.`when`(contactRepository.getContactsWithFilterByFilter(filter.filter))
            .thenReturn(contactList)
        val result = detailsFilterUseCase.getQueryContactCallList(filter)
        assertEquals(numberDataList, result)
    }

    @Test
    fun filteredNumberDataListTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = TEST_NUMBER)), CallWithFilter().apply { call = Call(number = TEST_FILTER) })
        Mockito.`when`(contactRepository.filteredNumberDataList(filter, numberDataList, 0))
            .thenReturn(numberDataList)
        val result = detailsFilterUseCase.filteredNumberDataList(filter, numberDataList, 0)
        assertEquals(TEST_NUMBER, (result[0] as ContactWithFilter).contact?.number)
    }

    @Test
    fun filteredCallsByFilterTest() = runTest {
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call = FilteredCall().apply { this.number = TEST_NUMBER } })
        Mockito.`when`(filteredCallRepository.filteredCallsByFilter(TEST_FILTER))
            .thenReturn(filteredCallList)
        val result = detailsFilterUseCase.filteredCallsByFilter(TEST_FILTER)
        assertEquals(TEST_NUMBER, result[0].call?.number)
    }

    @Test
    fun deleteFilterTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(filterRepository).deleteFilterList(eq(listOf(filter)), any())
        detailsFilterUseCase.deleteFilter(filter, resultMock)
        verify(resultMock).invoke()
    }

    @Test
    fun updateFilterTest() = runTest {
        val filter = Filter(filter = TEST_FILTER)
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(filterRepository).updateFilter(eq(filter), any())
        detailsFilterUseCase.updateFilter(filter, resultMock)
        verify(resultMock).invoke()
    }

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        detailsFilterUseCase = DetailsFilterUseCaseImpl(contactRepository, filterRepository, logCallRepository, filteredCallRepository)
    }
}