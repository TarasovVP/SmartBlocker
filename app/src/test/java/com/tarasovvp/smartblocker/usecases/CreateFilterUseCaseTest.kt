package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumbers
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecases.CreateFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.create.CreateFilterUseCaseImpl
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class CreateFilterUseCaseTest {

    @MockK
    private lateinit var contactRepository: ContactRepository

    @MockK
    private lateinit var countryCodeRepository: CountryCodeRepository

    @MockK
    private lateinit var filterRepository: FilterRepository

    @MockK
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @MockK
    private lateinit var logCallRepository: LogCallRepository

    @MockK(relaxed = true)
    private lateinit var resultMock: () -> Unit

    private lateinit var createFilterUseCase: CreateFilterUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        createFilterUseCase = CreateFilterUseCaseImpl(contactRepository, countryCodeRepository, filterRepository, realDataBaseRepository, logCallRepository)
    }

    @Test
    fun getCountryCodeWithCodeTest() = runBlocking {
        val countryCode = 123
        val expectedCountryCode = CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_COUNTRY)
        coEvery { countryCodeRepository.getCountryCodeByCode(countryCode) } returns expectedCountryCode
        val result =  createFilterUseCase.getCountryCodeWithCode(countryCode)
        assertEquals(result?.country, TEST_COUNTRY)
    }

    @Test
    fun getNumberDataListTest() = runBlocking {
        val callList = listOf(LogCallWithFilter().apply { call = LogCall().apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall().apply { number = "2"} })
        val contactList = listOf(ContactWithFilter(contact =  Contact(number = "1")), ContactWithFilter(contact =  Contact(number = "1")))
        val numberDataUIModelList = ArrayList<NumberDataUIModel>().apply {
            addAll(contactList)
            addAll(callList)
        }.sortedBy {
            if (it is ContactWithFilter) it.contact?.number else if (it is LogCallWithFilter) it.call?.number else String.EMPTY
        }
        coEvery { logCallRepository.allDistinctCallsWithFilter() } returns callList
        coEvery { contactRepository.allContactWithFilters() } returns contactList
        val result = createFilterUseCase.getNumberDataList()
        assertEquals(numberDataUIModelList, result)
    }

    @Test
    fun checkFilterExistTest() = runBlocking {
        val filterWithFilteredNumbers = FilterWithFilteredNumbers(filter = Filter(filter = UnitTestUtils.TEST_FILTER))
        coEvery { filterRepository.getFilter(filterWithFilteredNumbers) } returns filterWithFilteredNumbers
        val result = createFilterUseCase.checkFilterExist(filterWithFilteredNumbers)
        assertEquals(UnitTestUtils.TEST_FILTER, result?.filter?.filter)
    }

    @Test
    fun filterNumberDataListTest() = runBlocking {
        val filterWithFilteredNumbers = FilterWithFilteredNumbers(filter = Filter(filter = UnitTestUtils.TEST_FILTER))
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = TEST_NUMBER)), CallWithFilter().apply { call = Call(number = TEST_NUMBER) })
        coEvery { contactRepository.filteredNumberDataList(filterWithFilteredNumbers.filter, numberDataList, 0) } returns numberDataList
        val result = createFilterUseCase.filterNumberDataList(filterWithFilteredNumbers, numberDataList, 0)
        assertEquals(TEST_NUMBER, (result[0] as ContactWithFilter).contact?.number)
    }

    @Test
    fun createFilterTest() = runBlocking {
        val filter = Filter(filter = UnitTestUtils.TEST_FILTER)
        every { realDataBaseRepository.insertFilter(eq(filter), any()) } coAnswers {
            val callback = secondArg<() -> Unit>()
            callback.invoke()
        }
        coEvery { filterRepository.insertFilter(eq(filter)) } just Runs

        createFilterUseCase.createFilter(filter, true, resultMock)
        verify { realDataBaseRepository.insertFilter(eq(filter), any()) }
        coVerify { filterRepository.insertFilter(eq(filter)) }
        verify { resultMock.invoke() }

        createFilterUseCase.createFilter(filter, false, resultMock)

        verify(exactly = 1) { realDataBaseRepository.insertFilter(eq(filter), any()) }
        coVerify(exactly = 2) { filterRepository.insertFilter(eq(filter)) }
        verify(exactly = 2) { resultMock.invoke() }
    }

    @Test
    fun updateFilterTest() = runBlocking {
        val filter = Filter(filter = UnitTestUtils.TEST_FILTER)
        every { realDataBaseRepository.insertFilter(eq(filter), any()) } coAnswers {
            val callback = secondArg<() -> Unit>()
            callback.invoke()
        }
        coEvery { filterRepository.updateFilter(eq(filter)) } just Runs

        createFilterUseCase.updateFilter(filter, true, resultMock)

        verify { realDataBaseRepository.insertFilter(eq(filter), any()) }
        coVerify { filterRepository.updateFilter(eq(filter)) }
        verify { resultMock.invoke() }

        createFilterUseCase.updateFilter(filter, false, resultMock)

        verify(exactly = 1) { realDataBaseRepository.insertFilter(eq(filter), any()) }
        coVerify(exactly = 2) { filterRepository.updateFilter(eq(filter)) }
        verify(exactly = 2) { resultMock.invoke() }
    }

    @Test
    fun deleteFilterTest() = runBlocking {
        val filter = Filter(filter = UnitTestUtils.TEST_FILTER)
        every { realDataBaseRepository.deleteFilterList(eq(listOf(filter)), any()) } coAnswers {
            val callback = secondArg<() -> Unit>()
            callback.invoke()
        }
        coEvery { filterRepository.deleteFilterList(eq(listOf(filter))) } just Runs

        createFilterUseCase.deleteFilter(filter, true, resultMock)

        verify { realDataBaseRepository.deleteFilterList(eq(listOf(filter)), any()) }
        coVerify { filterRepository.deleteFilterList(eq(listOf(filter))) }
        verify { resultMock.invoke() }

        createFilterUseCase.deleteFilter(filter, false, resultMock)

        verify(exactly = 1) { realDataBaseRepository.deleteFilterList(eq(listOf(filter)), any()) }
        coVerify(exactly = 2) { filterRepository.deleteFilterList(eq(listOf(filter))) }
        verify(exactly = 2) { resultMock.invoke() }
    }
}