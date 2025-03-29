package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.usecases.CountryCodeSearchUseCase
import com.tarasovvp.smartblocker.presentation.dialogs.country_code_search_dialog.CountryCodeSearchViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class CountryCodeSearchViewModelUnitTest : BaseViewModelUnitTest<CountryCodeSearchViewModel>() {
    @MockK
    private lateinit var useCase: CountryCodeSearchUseCase

    @MockK
    private lateinit var countryCodeUIMapper: CountryCodeUIMapper

    override fun createViewModel(): CountryCodeSearchViewModel {
        return CountryCodeSearchViewModel(application, useCase, countryCodeUIMapper)
    }

    @Test
    fun getCountryCodeList() =
        runTest {
            val countryCodeList =
                listOf(
                    CountryCode(countryCode = TEST_COUNTRY_CODE, country = TEST_NUMBER),
                    CountryCode(countryCode = "+123", country = "AI"),
                )
            val countryCodeUIModelList =
                listOf(
                    CountryCodeUIModel(countryCode = TEST_COUNTRY_CODE, country = TEST_NUMBER),
                    CountryCodeUIModel(countryCode = "+123", country = "AI"),
                )
            coEvery { useCase.getCountryCodeList() } returns countryCodeList
            every { countryCodeUIMapper.mapToUIModelList(countryCodeList) } returns countryCodeUIModelList
            viewModel.getCountryCodeList()
            advanceUntilIdle()
            coVerify { useCase.getCountryCodeList() }
            assertEquals(countryCodeUIModelList, viewModel.countryCodeListLiveData.getOrAwaitValue())
        }
}
