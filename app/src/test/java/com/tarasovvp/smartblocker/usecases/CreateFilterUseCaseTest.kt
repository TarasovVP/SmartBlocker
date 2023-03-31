package com.tarasovvp.smartblocker.usecases

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.TestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.TestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecase.number.create.CreateFilterUseCase
import com.tarasovvp.smartblocker.domain.usecase.number.create.CreateFilterUseCaseImpl
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
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
class CreateFilterUseCaseTest {

    @Mock
    private lateinit var contactRepository: ContactRepository

    @Mock
    private lateinit var countryCodeRepository: CountryCodeRepository

    @Mock
    private lateinit var filterRepository: FilterRepository

    @Mock
    private lateinit var logCallRepository: LogCallRepository

    @Mock
    private lateinit var resultMock: () -> Unit

    private lateinit var createFilterUseCase: CreateFilterUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        createFilterUseCase = CreateFilterUseCaseImpl(contactRepository, countryCodeRepository, filterRepository, logCallRepository)
    }

    @Test
    fun getCountryCodeWithCountryTest() = runTest {
        val expectedCountryCode = CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_COUNTRY)
        Mockito.`when`(countryCodeRepository.getCountryCodeWithCountry(TEST_COUNTRY))
            .thenReturn(expectedCountryCode)

        val resultCountry = createFilterUseCase.getCountryCodeWithCountry(TEST_COUNTRY)
        assertEquals(expectedCountryCode.country, resultCountry?.country)
        assertEquals(expectedCountryCode.countryCode, resultCountry?.countryCode)
    }

    @Test
    fun getCountryCodeWithCodeTest() = runTest {
        val countryCode = 123
        val expectedCountryCode = CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_COUNTRY)
        Mockito.`when`(countryCodeRepository.getCountryCodeWithCode(countryCode))
            .thenReturn(expectedCountryCode)

        val result =  createFilterUseCase.getCountryCodeWithCode(countryCode)
        assertEquals(result?.country, TEST_COUNTRY)
    }

    @Test
    fun getNumberDataListTest() = runTest {
        val callList = listOf(LogCallWithFilter().apply { call = LogCall().apply { number = "1" } }, LogCallWithFilter().apply { call = LogCall().apply { number = "2"} })
        val contactList = listOf(ContactWithFilter(contact =  Contact(number = "1")), ContactWithFilter(contact =  Contact(number = "1")))
        val numberDataList = ArrayList<NumberData>().apply {
            addAll(contactList)
            addAll(callList)
        }.sortedBy {
            if (it is ContactWithFilter) it.contact?.number else if (it is LogCallWithFilter) it.call?.number else String.EMPTY
        }
        Mockito.`when`(logCallRepository.allCallNumberWithFilter())
            .thenReturn(callList)
        Mockito.`when`(contactRepository.getContactsWithFilters())
            .thenReturn(contactList)
        val result = createFilterUseCase.getNumberDataList()
        assertEquals(numberDataList, result)
    }

    @Test
    fun checkFilterExistTest() = runTest {
        val filterWithCountryCode = FilterWithCountryCode(filter = Filter(filter = TestUtils.TEST_FILTER))
        Mockito.`when`(filterRepository.getFilter(filterWithCountryCode))
            .thenReturn(filterWithCountryCode)
        val result = createFilterUseCase.checkFilterExist(filterWithCountryCode)
        assertEquals(TestUtils.TEST_FILTER, result?.filter?.filter)
    }

    @Test
    fun filterNumberDataListTest() = runTest {
        val filterWithCountryCode = FilterWithCountryCode(filter = Filter(filter = TestUtils.TEST_FILTER))
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = TEST_NUMBER)), CallWithFilter().apply { call = Call(number = TEST_NUMBER) })
        Mockito.`when`(contactRepository.filteredNumberDataList(filterWithCountryCode.filter, numberDataList, 0))
            .thenReturn(numberDataList)
        val result = createFilterUseCase.filterNumberDataList(filterWithCountryCode, numberDataList, 0)
        assertEquals(TEST_NUMBER, (result[0] as ContactWithFilter).contact?.number)
    }

    @Test
    fun createFilterTest() = runTest {
        val filter = Filter(filter = TestUtils.TEST_FILTER)
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(filterRepository).insertFilter(eq(filter), any())
        createFilterUseCase.createFilter(filter, resultMock)
        verify(resultMock).invoke()
    }

    @Test
    fun updateFilterTest() = runTest {
        val filter = Filter(filter = TestUtils.TEST_FILTER)
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(filterRepository).updateFilter(eq(filter), any())
        createFilterUseCase.updateFilter(filter, resultMock)
        verify(resultMock).invoke()
    }

    @Test
    fun deleteFilterTest() = runTest {
        val filter = Filter(filter = TestUtils.TEST_FILTER)
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(filterRepository).deleteFilterList(eq(listOf(filter)), any())
        createFilterUseCase.deleteFilter(filter, resultMock)
        verify(resultMock).invoke()
    }
}