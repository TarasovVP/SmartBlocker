package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NAME
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.entities.dbentities.Contact
import com.tarasovvp.smartblocker.domain.entities.dbentities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.dbentities.Filter
import com.tarasovvp.smartblocker.domain.entities.dbentities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.dbentities.LogCall
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.domain.usecases.MainUseCase
import com.tarasovvp.smartblocker.presentation.main.MainViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelUnitTest : BaseViewModelUnitTest<MainViewModel>() {
    @MockK
    private lateinit var mainUseCase: MainUseCase

    override fun createViewModel() = MainViewModel(application, mainUseCase)

    @Test
    fun getOnBoardingSeenTest() =
        runTest {
            val onBoardingSeen = true
            coEvery { mainUseCase.getOnBoardingSeen() } returns flowOf(onBoardingSeen)
            viewModel.getOnBoardingSeen()
            advanceUntilIdle()
            coVerify { mainUseCase.getOnBoardingSeen() }
            assertEquals(onBoardingSeen, viewModel.onBoardingSeenLiveData.getOrAwaitValue())
        }

    @Test
    fun getBlockerTurnOnTest() =
        runTest {
            val blockerTurnOn = true
            coEvery { mainUseCase.getBlockerTurnOn() } returns flowOf(blockerTurnOn)
            viewModel.getBlockerTurnOn()
            advanceUntilIdle()
            coVerify { mainUseCase.getBlockerTurnOn() }
            assertEquals(blockerTurnOn, viewModel.blockerTurnOnLiveData.getOrAwaitValue())
        }

    @Test
    fun getCurrentUserTest() =
        runTest {
            val currentUser = CurrentUser()
            val expectedResult = Result.Success(currentUser)
            coEvery { mainUseCase.getCurrentUser(any()) } answers {
                val result = firstArg<(Result<CurrentUser>) -> Unit>()
                result.invoke(expectedResult)
            }
            viewModel.getCurrentUser()
            advanceUntilIdle()
            coVerify { mainUseCase.getCurrentUser(any()) }
        }

    @Test
    fun insertUserFiltersTest() =
        runTest {
            val filterList = listOf(Filter(filter = TEST_FILTER), Filter(filter = "mockFilter2"))
            coEvery { mainUseCase.insertAllFilters(filterList) } just Runs
            viewModel.insertUserFilters(filterList)
            coVerify { mainUseCase.insertAllFilters(filterList) }
        }

    @Test
    fun insertUserFilteredCallsTest() =
        runTest {
            val filteredCallList =
                listOf(
                    FilteredCall().apply { number = TEST_FILTER },
                    FilteredCall().apply { number = TEST_FILTER },
                )
            coEvery { mainUseCase.insertAllFilteredCalls(filteredCallList) } just Runs
            viewModel.insertUserFilteredCalls(filteredCallList)
            coVerify { mainUseCase.insertAllFilteredCalls(filteredCallList) }
        }

    @Test
    fun setCountryCodeDataTest() =
        runTest {
            val countryCodeList = listOf(CountryCode(country = TEST_COUNTRY))
            coEvery { mainUseCase.getSystemCountryCodes(any()) } returns countryCodeList
            coEvery { mainUseCase.insertAllCountryCodes(countryCodeList) } just Runs
            coEvery { mainUseCase.getCurrentCountryCode() } returns flowOf(countryCodeList.first())
            coEvery { mainUseCase.setCurrentCountryCode(countryCodeList.first()) } just Runs
            viewModel.setCountryCodeData()
            coVerify { mainUseCase.getSystemCountryCodes(any()) }
            coVerify { mainUseCase.insertAllCountryCodes(countryCodeList) }
        }

    @Test
    fun setCurrentCountryCodeTest() =
        runTest {
            val countryCode = CountryCode()
            coEvery { mainUseCase.getCurrentCountryCode() } returns flowOf(countryCode)
            coEvery { mainUseCase.setCurrentCountryCode(countryCode) } just Runs
            viewModel.setCurrentCountryCode(listOf(countryCode))
            advanceUntilIdle()
            coVerify { mainUseCase.getCurrentCountryCode() }

            coEvery { mainUseCase.getCurrentCountryCode() } returns flowOf(null)
            coEvery { mainUseCase.setCurrentCountryCode(countryCode) } just Runs
            viewModel.setCurrentCountryCode(listOf(countryCode))
            advanceUntilIdle()
            coVerify { mainUseCase.getCurrentCountryCode() }
            coVerify { mainUseCase.setCurrentCountryCode(countryCode) }
        }

    @Test
    fun setContactDataTest() =
        runTest {
            val contactList = listOf(Contact(name = TEST_NAME))
            coEvery { mainUseCase.getSystemContacts(eq(application), any()) } returns contactList
            coEvery { mainUseCase.insertAllContacts(contactList) } just Runs
            viewModel.setContactData()
            coVerify { mainUseCase.getSystemContacts(eq(application), any()) }
            coVerify { mainUseCase.insertAllContacts(contactList) }
        }

    @Test
    fun setLogCallDataTest() =
        runTest {
            val logCallList = listOf(LogCall().apply { number = TEST_NUMBER })
            coEvery { mainUseCase.getSystemLogCalls(any(), any()) } answers {
                val progressCallback = secondArg<(Int, Int) -> Unit>()
                progressCallback.invoke(logCallList.size, 0)
                logCallList
            }
            coEvery { mainUseCase.insertAllLogCalls(logCallList) } just Runs
            viewModel.setLogCallData()
            coVerify { mainUseCase.getSystemLogCalls(any(), any()) }
            coVerify { mainUseCase.insertAllLogCalls(logCallList) }
        }
}
