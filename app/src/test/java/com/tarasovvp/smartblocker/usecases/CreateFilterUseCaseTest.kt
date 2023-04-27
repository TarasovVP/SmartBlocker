package com.tarasovvp.smartblocker.usecases

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.UnitTestUtils
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
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
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @Mock
    private lateinit var logCallRepository: LogCallRepository

    @Mock
    private lateinit var resultMock: () -> Unit

    private lateinit var createFilterUseCase: CreateFilterUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        createFilterUseCase = CreateFilterUseCaseImpl(contactRepository, countryCodeRepository, filterRepository, realDataBaseRepository, logCallRepository)
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
        val filterWithCountryCode = FilterWithCountryCode(filter = Filter(filter = UnitTestUtils.TEST_FILTER))
        Mockito.`when`(filterRepository.getFilter(filterWithCountryCode))
            .thenReturn(filterWithCountryCode)
        val result = createFilterUseCase.checkFilterExist(filterWithCountryCode)
        assertEquals(UnitTestUtils.TEST_FILTER, result?.filter?.filter)
    }

    @Test
    fun filterNumberDataListTest() = runTest {
        val filterWithCountryCode = FilterWithCountryCode(filter = Filter(filter = UnitTestUtils.TEST_FILTER))
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = TEST_NUMBER)), CallWithFilter().apply { call = Call(number = TEST_NUMBER) })
        Mockito.`when`(contactRepository.filteredNumberDataList(filterWithCountryCode.filter, numberDataList, 0))
            .thenReturn(numberDataList)
        val result = createFilterUseCase.filterNumberDataList(filterWithCountryCode, numberDataList, 0)
        assertEquals(TEST_NUMBER, (result[0] as ContactWithFilter).contact?.number)
    }

    @Test
    fun createFilterTest() = runTest {
        val filter = Filter(filter = UnitTestUtils.TEST_FILTER)
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(realDataBaseRepository).insertFilter(eq(filter), any())
        Mockito.`when`(filterRepository.insertFilter(eq(filter))).thenReturn(Unit)
        createFilterUseCase.createFilter(filter, true, resultMock)
        verify(realDataBaseRepository).insertFilter(eq(filter), any())
        verify(filterRepository).insertFilter(eq(filter))
        verify(resultMock).invoke()
        createFilterUseCase.createFilter(filter, false, resultMock)
        verify(realDataBaseRepository, times(1)).insertFilter(eq(filter), any())
        verify(filterRepository, times(2)).insertFilter(eq(filter))
        verify(resultMock, times(2)).invoke()
    }

    @Test
    fun updateFilterTest() = runTest {
        val filter = Filter(filter = UnitTestUtils.TEST_FILTER)
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(realDataBaseRepository).insertFilter(eq(filter), any())
        Mockito.`when`(filterRepository.updateFilter(eq(filter))).thenReturn(Unit)
        createFilterUseCase.updateFilter(filter, true, resultMock)
        verify(realDataBaseRepository).insertFilter(eq(filter), any())
        verify(filterRepository).updateFilter(eq(filter))
        verify(resultMock).invoke()
        createFilterUseCase.updateFilter(filter, false, resultMock)
        verify(realDataBaseRepository, times(1)).insertFilter(eq(filter), any())
        verify(filterRepository, times(2)).updateFilter(eq(filter))
        verify(resultMock, times(2)).invoke()
    }

    @Test
    fun deleteFilterTest() = runTest {
        val filter = Filter(filter = UnitTestUtils.TEST_FILTER)
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(realDataBaseRepository).deleteFilterList(eq(listOf(filter)), any())
        Mockito.`when`(filterRepository.deleteFilterList(eq(listOf(filter)))).thenReturn(Unit)
        createFilterUseCase.deleteFilter(filter, true, resultMock)
        verify(realDataBaseRepository).deleteFilterList(eq(listOf(filter)), any())
        verify(filterRepository).deleteFilterList(eq(listOf(filter)))
        verify(resultMock).invoke()
        createFilterUseCase.deleteFilter(filter, false, resultMock)
        verify(realDataBaseRepository, times(1)).deleteFilterList(eq(listOf(filter)), any())
        verify(filterRepository, times(2)).deleteFilterList(eq(listOf(filter)))
        verify(resultMock, times(2)).invoke()
    }
}