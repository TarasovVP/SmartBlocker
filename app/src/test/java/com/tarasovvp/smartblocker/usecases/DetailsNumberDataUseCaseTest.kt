package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.TestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.TestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.database_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.usecase.number.details.details_number_data.DetailsNumberDataUseCase
import com.tarasovvp.smartblocker.domain.usecase.number.details.details_number_data.DetailsNumberDataUseCaseImpl
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
class DetailsNumberDataUseCaseTest {

    @Mock
    private lateinit var countryCodeRepository: CountryCodeRepository

    @Mock
    private lateinit var filterRepository: FilterRepository

    @Mock
    private lateinit var filteredCallRepository: FilteredCallRepository

    private lateinit var detailsNumberDataUseCase: DetailsNumberDataUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        detailsNumberDataUseCase = DetailsNumberDataUseCaseImpl(countryCodeRepository, filterRepository, filteredCallRepository)
    }

    @Test
    fun filterListWithNumberTest() = runTest {
        val filterList = listOf(FilterWithCountryCode(filter = Filter(filter = TestUtils.TEST_FILTER)), FilterWithCountryCode(filter = Filter(filter = "mockFilter2")))
        Mockito.`when`(filterRepository.queryFilterList(TEST_NUMBER))
            .thenReturn(filterList)
        val result = detailsNumberDataUseCase.filterListWithNumber(TEST_NUMBER)
        assertEquals(TestUtils.TEST_FILTER, (result[0] as FilterWithCountryCode).filter?.filter)
    }

    @Test
    fun filteredCallsByNumberTest() = runTest {
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call = FilteredCall().apply { this.number =
            TEST_NUMBER
        } })
        Mockito.`when`(filteredCallRepository.filteredCallsByNumber(TEST_NUMBER))
            .thenReturn(filteredCallList)
        val result = detailsNumberDataUseCase.filteredCallsByNumber(TEST_NUMBER)
        assertEquals(TEST_NUMBER, (result[0] as FilteredCallWithFilter).call?.number)
    }

    @Test
    fun getCountryCodeTest() = runTest {
        val countryCode = 123
        val expectedCountryCode = CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_COUNTRY)
        Mockito.`when`(countryCodeRepository.getCountryCodeWithCode(countryCode))
            .thenReturn(expectedCountryCode)

        val result = detailsNumberDataUseCase.getCountryCode(countryCode)
        assertEquals(TEST_COUNTRY, result?.country)
    }
}