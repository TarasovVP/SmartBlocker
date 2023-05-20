package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumbers
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.usecases.DetailsNumberDataUseCase
import com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data.DetailsNumberDataUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
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

    @MockK
    private lateinit var dataStoreRepository: DataStoreRepository

    private lateinit var detailsNumberDataUseCase: DetailsNumberDataUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        detailsNumberDataUseCase = DetailsNumberDataUseCaseImpl(countryCodeRepository, filterRepository, filteredCallRepository, dataStoreRepository)
    }

    @Test
    fun filterWithFilteredNumbersTest() = runBlocking {
        val filterList = listOf(FilterWithFilteredNumbers(filter = Filter(filter = UnitTestUtils.TEST_FILTER)), FilterWithFilteredNumbers(filter = Filter(filter = "mockFilter2")))
        coEvery { filterRepository.allFilterWithFilteredNumbersByNumber(TEST_NUMBER) } returns filterList
        val result = detailsNumberDataUseCase.allFilterWithFilteredNumbersByNumber(TEST_NUMBER)
        assertEquals(filterList, result)
    }

    @Test
    fun filteredCallsByNumberTest() = runBlocking {
        val filteredCallList = listOf(CallWithFilter().apply { call = FilteredCall().apply { this.number =
            TEST_NUMBER
        } })
        coEvery { filteredCallRepository.allFilteredCallsByNumber(TEST_NUMBER) } returns filteredCallList
        val result = detailsNumberDataUseCase.allFilteredCallsByNumber(TEST_NUMBER)
        assertEquals(filteredCallList, result)
    }

    @Test
    fun getCountryCodeTest() = runBlocking {
        val countryCode = 123
        val expectedCountryCode = CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_COUNTRY)
        coEvery { countryCodeRepository.getCountryCodeByCode(countryCode) } returns expectedCountryCode
        val result = detailsNumberDataUseCase.getCountryCodeByCode(countryCode)
        assertEquals(expectedCountryCode, result)
    }

    @Test
    fun getBlockHiddenTest() = runBlocking {
        val blockHidden = true
        val flow = flowOf(blockHidden)
        coEvery { dataStoreRepository.blockHidden() } returns flow
        val result = detailsNumberDataUseCase.getBlockHidden().single()
        assertEquals(blockHidden, result)
        coVerify { dataStoreRepository.blockHidden() }
    }
}