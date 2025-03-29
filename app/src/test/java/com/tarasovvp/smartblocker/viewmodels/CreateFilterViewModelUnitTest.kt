package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.CreateFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.create.CreateFilterViewModel
import com.tarasovvp.smartblocker.presentation.mappers.ContactWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithCountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class CreateFilterViewModelUnitTest : BaseViewModelUnitTest<CreateFilterViewModel>() {
    @MockK
    private lateinit var useCase: CreateFilterUseCase

    @MockK
    private lateinit var filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper

    @MockK
    private lateinit var contactWithFilterUIMapper: ContactWithFilterUIMapper

    override fun createViewModel() =
        CreateFilterViewModel(
            application,
            useCase,
            filterWithFilteredNumberUIMapper,
            contactWithFilterUIMapper,
        )

    @Test
    fun getMatchedContactWithFilterListTest() =
        runTest {
            val filterWithCountryCodeUIModel =
                FilterWithCountryCodeUIModel().apply {
                    filterWithFilteredNumberUIModel.filter = TEST_FILTER
                    filterWithFilteredNumberUIModel.conditionType =
                        FilterCondition.FILTER_CONDITION_CONTAIN.ordinal
                    countryCodeUIModel.country = TEST_COUNTRY
                    countryCodeUIModel.countryCode = TEST_COUNTRY_CODE
                }
            val contactWithFilters =
                arrayListOf(
                    ContactWithFilter(contact = Contact(number = TEST_NUMBER)),
                    ContactWithFilter(contact = Contact(number = TEST_NUMBER)),
                )
            val contactWithFilterUIModels = arrayListOf(ContactWithFilterUIModel(number = TEST_NUMBER))
            coEvery {
                useCase.allContactsWithFiltersByCreateFilter(
                    TEST_FILTER,
                    TEST_COUNTRY,
                    TEST_COUNTRY_CODE,
                    true,
                )
            } returns contactWithFilters
            coEvery { contactWithFilterUIMapper.mapToUIModelList(contactWithFilters) } returns contactWithFilterUIModels
            viewModel.getMatchedContactWithFilterList(filterWithCountryCodeUIModel)
            advanceUntilIdle()
            val result = viewModel.contactWithFilterLiveData.getOrAwaitValue()
            assertEquals(contactWithFilterUIModels, result)
            coVerify {
                useCase.allContactsWithFiltersByCreateFilter(
                    TEST_FILTER,
                    TEST_COUNTRY,
                    TEST_COUNTRY_CODE,
                    true,
                )
            }
            coVerify { contactWithFilterUIMapper.mapToUIModelList(contactWithFilters) }
        }

    @Test
    fun checkFilterExistTest() =
        runTest {
            val filterValue = TEST_FILTER
            val filter = FilterWithFilteredNumber(filter = Filter(filter = TEST_FILTER))
            val filterUIModel = FilterWithFilteredNumberUIModel(filter = TEST_FILTER)
            coEvery { useCase.getFilter(filterValue) } returns filter
            every { filterWithFilteredNumberUIMapper.mapToUIModel(filter) } returns filterUIModel
            viewModel.checkFilterExist(filterValue)
            advanceUntilIdle()
            coVerify { useCase.getFilter(filterValue) }
            verify { filterWithFilteredNumberUIMapper.mapToUIModel(filter) }
            assertEquals(filterUIModel, viewModel.existingFilterLiveData.getOrAwaitValue())
        }

    @Test
    fun createFilterTest() =
        runTest {
            val expectedResult = Result.Success<Unit>()
            val filterWithFilteredNumber =
                FilterWithFilteredNumber(filter = Filter(filter = TEST_FILTER))
            val filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(filter = TEST_FILTER)
            every { application.isNetworkAvailable } returns true
            coEvery {
                useCase.createFilter(
                    eq(filterWithFilteredNumber.filter ?: Filter()),
                    eq(true),
                    any(),
                )
            } answers {
                val result = thirdArg<(Result<Unit>) -> Unit>()
                result.invoke(expectedResult)
            }
            every { filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithFilteredNumberUIModel) } returns filterWithFilteredNumber
            viewModel.createFilter(filterWithFilteredNumberUIModel)
            advanceUntilIdle()
            coVerify {
                useCase.createFilter(
                    eq(filterWithFilteredNumber.filter ?: Filter()),
                    eq(true),
                    any(),
                )
            }
            verify { filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithFilteredNumberUIModel) }
            assertEquals(
                filterWithFilteredNumberUIModel,
                viewModel.filterActionLiveData.getOrAwaitValue(),
            )
        }

    @Test
    fun updateFilterTest() =
        runTest {
            val expectedResult = Result.Success<Unit>()
            val filterWithFilteredNumber =
                FilterWithFilteredNumber(filter = Filter(filter = TEST_FILTER))
            val filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(filter = TEST_FILTER)
            every { application.isNetworkAvailable } returns true
            coEvery {
                useCase.updateFilter(
                    eq(filterWithFilteredNumber.filter ?: Filter()),
                    eq(true),
                    any(),
                )
            } answers {
                val result = thirdArg<(Result<Unit>) -> Unit>()
                result.invoke(expectedResult)
            }
            every { filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithFilteredNumberUIModel) } returns filterWithFilteredNumber
            viewModel.updateFilter(filterWithFilteredNumberUIModel)
            advanceUntilIdle()
            coVerify {
                useCase.updateFilter(
                    eq(filterWithFilteredNumber.filter ?: Filter()),
                    eq(true),
                    any(),
                )
            }
            verify { filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithFilteredNumberUIModel) }
            assertEquals(
                filterWithFilteredNumberUIModel,
                viewModel.filterActionLiveData.getOrAwaitValue(),
            )
        }

    @Test
    fun deleteFilterTest() =
        runTest {
            val expectedResult = Result.Success<Unit>()
            val filterWithFilteredNumber =
                FilterWithFilteredNumber(filter = Filter(filter = TEST_FILTER))
            val filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(filter = TEST_FILTER)
            every { application.isNetworkAvailable } returns true
            coEvery {
                useCase.deleteFilter(
                    eq(filterWithFilteredNumber.filter ?: Filter()),
                    eq(true),
                    any(),
                )
            } answers {
                val result = thirdArg<(Result<Unit>) -> Unit>()
                result.invoke(expectedResult)
            }
            every { filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithFilteredNumberUIModel) } returns filterWithFilteredNumber
            viewModel.deleteFilter(filterWithFilteredNumberUIModel)
            advanceUntilIdle()
            coVerify {
                useCase.deleteFilter(
                    eq(filterWithFilteredNumber.filter ?: Filter()),
                    eq(true),
                    any(),
                )
            }
            verify { filterWithFilteredNumberUIMapper.mapFromUIModel(filterWithFilteredNumberUIModel) }
            assertEquals(
                filterWithFilteredNumberUIModel,
                viewModel.filterActionLiveData.getOrAwaitValue(),
            )
        }
}
