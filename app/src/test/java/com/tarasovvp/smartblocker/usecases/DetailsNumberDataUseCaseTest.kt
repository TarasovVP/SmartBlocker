package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.usecases.DetailsNumberDataUseCase
import com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data.DetailsNumberDataUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class DetailsNumberDataUseCaseTest {

    @MockK
    private lateinit var countryCodeRepository: CountryCodeRepository

    @MockK
    private lateinit var filterRepository: FilterRepository

    @MockK
    private lateinit var filteredCallRepository: FilteredCallRepository

    private lateinit var detailsNumberDataUseCase: DetailsNumberDataUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        detailsNumberDataUseCase = DetailsNumberDataUseCaseImpl(countryCodeRepository, filterRepository, filteredCallRepository)
    }

    @Test
    fun filterListWithNumberTest() = runBlocking {
        val filterList = listOf(FilterWithCountryCode(filter = Filter(filter = UnitTestUtils.TEST_FILTER)), FilterWithCountryCode(filter = Filter(filter = "mockFilter2")))
        coEvery { filterRepository.queryFilterList(TEST_NUMBER) } returns filterList
        val result = detailsNumberDataUseCase.filterListWithNumber(TEST_NUMBER)
        assertEquals(UnitTestUtils.TEST_FILTER, (result[0] as FilterWithCountryCode).filter?.filter)
    }

    @Test
    fun filteredCallsByNumberTest() = runBlocking {
        val filteredCallList = listOf(FilteredCallWithFilter().apply { call = FilteredCall().apply { this.number =
            TEST_NUMBER
        } })
        coEvery { filteredCallRepository.filteredCallsByNumber(TEST_NUMBER) } returns filteredCallList
        val result = detailsNumberDataUseCase.filteredCallsByNumber(TEST_NUMBER)
        assertEquals(TEST_NUMBER, (result[0] as FilteredCallWithFilter).call?.number)
    }

    @Test
    fun getCountryCodeTest() = runBlocking {
        val countryCode = 123
        val expectedCountryCode = CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_COUNTRY)
        coEvery { countryCodeRepository.getCountryCodeWithCode(countryCode) } returns expectedCountryCode
        val result = detailsNumberDataUseCase.getCountryCode(countryCode)
        assertEquals(TEST_COUNTRY, result?.country)
    }
}