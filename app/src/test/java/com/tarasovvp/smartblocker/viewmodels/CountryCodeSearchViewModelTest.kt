package com.tarasovvp.smartblocker.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tarasovvp.smartblocker.TestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.usecase.countrycode_search.CountryCodeSearchUseCase
import com.tarasovvp.smartblocker.presentation.dialogs.country_code_search_dialog.CountryCodeSearchViewModel
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CountryCodeSearchViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var countryCodeSearchUseCase: CountryCodeSearchUseCase

    private lateinit var viewModel: CountryCodeSearchViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel =
            CountryCodeSearchViewModel(application, countryCodeSearchUseCase)
    }

    @Test
    fun getCountryCodeList() = runTest {
        val country = "UA"
        val countryCode = "+380"
        val countryCodeList = listOf(CountryCode(countryCode = countryCode, country = country), CountryCode(countryCode = "+123", country = "AI"))
        Mockito.`when`(countryCodeSearchUseCase.getCountryCodeList())
            .thenReturn(countryCodeList)

        viewModel.getCountryCodeList()
        advanceUntilIdle()
        val result = viewModel.countryCodeListLiveData.getOrAwaitValue()
        assertEquals(country, result[0].country)
        assertEquals(countryCode, result[0].countryCode)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}