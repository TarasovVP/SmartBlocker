package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.domain.entities.models.NumberData
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecases.DetailsFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.details.details_filter.DetailsFilterUseCaseImpl
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class DetailsFilterUseCaseTest {

    @MockK
    private lateinit var contactRepository: ContactRepository

    @MockK
    private lateinit var filterRepository: FilterRepository

    @MockK
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @MockK
    private lateinit var logCallRepository: LogCallRepository

    @MockK
    private lateinit var filteredCallRepository: FilteredCallRepository

    @MockK(relaxed = true)
    private lateinit var resultMock: () -> Unit

    private lateinit var detailsFilterUseCase: DetailsFilterUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        detailsFilterUseCase = DetailsFilterUseCaseImpl(contactRepository, filterRepository, realDataBaseRepository, logCallRepository, filteredCallRepository)
    }

    @Test
    fun getQueryContactCallListTest() = runBlocking {
        val filter = Filter(filter = TEST_FILTER)
        val callList = listOf(LogCallWithFilter().apply { call = LogCall().apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall().apply { number = "2"} })
        val contactList = listOf(ContactWithFilter(contact =  Contact(number = "1")), ContactWithFilter(contact =  Contact(number = "1")))
        val numberDataList = ArrayList<NumberData>().apply {
            addAll(callList)
            addAll(contactList)
        }.sortedBy {
            if (it is ContactWithFilter) it.contact?.number else if (it is LogCallWithFilter) it.call?.number else String.EMPTY
        }
        coEvery { logCallRepository.allCallWithFilterByFilter(filter.filter) } returns callList
        coEvery { contactRepository.getContactsWithFilterByFilter(filter.filter) } returns contactList
        val result = detailsFilterUseCase.getQueryContactCallList(filter)
        assertEquals(numberDataList, result)
    }

    @Test
    fun filteredNumberDataListTest() = runBlocking {
        val filter = Filter(filter = TEST_FILTER)
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = TEST_NUMBER)), CallWithFilter().apply { call = Call(number = TEST_FILTER) })
        coEvery { contactRepository.filteredNumberDataList(filter, numberDataList, 0) } returns numberDataList
        val result = detailsFilterUseCase.filteredNumberDataList(filter, numberDataList, 0)
        assertEquals(TEST_NUMBER, (result[0] as ContactWithFilter).contact?.number)
    }

    @Test
    fun filteredCallsByFilterTest() = runBlocking {
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call = FilteredCall().apply { this.number = TEST_NUMBER } })
        coEvery { filteredCallRepository.filteredCallsByFilter(TEST_FILTER) } returns filteredCallList
        val result = detailsFilterUseCase.filteredCallsByFilter(TEST_FILTER)
        assertEquals(TEST_NUMBER, result[0].call?.number)
    }

    @Test
    fun deleteFilterTest() = runBlocking {
        val filter = Filter(filter = TEST_FILTER)
        every { realDataBaseRepository.deleteFilterList(eq(listOf(filter)), any()) } coAnswers {
            val callback = secondArg<() -> Unit>()
            callback.invoke()
        }
        coEvery { filterRepository.deleteFilterList(eq(listOf(filter))) } just Runs

        detailsFilterUseCase.deleteFilter(filter, true, resultMock)

        verify { realDataBaseRepository.deleteFilterList(eq(listOf(filter)), any()) }
        coVerify { filterRepository.deleteFilterList(eq(listOf(filter))) }
        verify { resultMock.invoke() }

        detailsFilterUseCase.deleteFilter(filter, false, resultMock)

        verify(exactly = 1) { realDataBaseRepository.deleteFilterList(eq(listOf(filter)), any()) }
        coVerify(exactly = 2) { filterRepository.deleteFilterList(eq(listOf(filter))) }
        verify(exactly = 2) { resultMock.invoke() }
    }

    @Test
    fun updateFilterTest() = runBlocking {
        val filter = Filter(filter = TEST_FILTER)
        every { realDataBaseRepository.insertFilter(eq(filter), any()) } coAnswers {
            val callback = secondArg<() -> Unit>()
            callback.invoke()
        }
        coEvery { filterRepository.updateFilter(eq(filter)) } just Runs

        detailsFilterUseCase.updateFilter(filter, true, resultMock)

        verify { realDataBaseRepository.insertFilter(eq(filter), any()) }
        coVerify { filterRepository.updateFilter(eq(filter)) }
        verify { resultMock.invoke() }

        detailsFilterUseCase.updateFilter(filter, false, resultMock)

        verify(exactly = 1) { realDataBaseRepository.insertFilter(eq(filter), any()) }
        coVerify(exactly = 2) { filterRepository.updateFilter(eq(filter)) }
        verify(exactly = 2) { resultMock.invoke() }
    }
}