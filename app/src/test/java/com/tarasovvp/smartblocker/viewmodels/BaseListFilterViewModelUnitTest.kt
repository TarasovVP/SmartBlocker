package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.ListFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.BaseListFilterViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class BaseListFilterViewModelUnitTest : BaseViewModelUnitTest<BaseListFilterViewModel>() {
    @MockK
    private lateinit var useCase: ListFilterUseCase

    @MockK
    private lateinit var filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper

    @MockK
    private lateinit var countryCodeUIMapper: CountryCodeUIMapper

    override fun createViewModel() =
        BaseListFilterViewModel(
            application,
            useCase,
            filterWithFilteredNumberUIMapper,
            countryCodeUIMapper,
        )

    @Test
    fun getFilterListTest() =
        runTest {
            val filterList =
                listOf(
                    FilterWithFilteredNumber(filter = Filter(filter = TEST_FILTER)),
                    FilterWithFilteredNumber(filter = Filter(filter = "mockFilter2")),
                )
            val filterUIModelList =
                listOf(
                    FilterWithFilteredNumberUIModel(filter = TEST_FILTER),
                    FilterWithFilteredNumberUIModel(filter = "mockFilter2"),
                )
            coEvery { useCase.allFilterWithFilteredNumbersByType(true) } returns filterList
            every { filterWithFilteredNumberUIMapper.mapToUIModelList(filterList) } returns filterUIModelList
            viewModel.getFilterList(isBlackList = true, refreshing = false)
            advanceUntilIdle()
            coVerify { useCase.allFilterWithFilteredNumbersByType(true) }
            verify { filterWithFilteredNumberUIMapper.mapToUIModelList(filterList) }
            assertEquals(filterUIModelList, viewModel.filterListLiveData.getOrAwaitValue())
        }

    @Test
    fun getFilteredFilterListTest() =
        runTest {
            val filterList =
                listOf(
                    FilterWithFilteredNumberUIModel(
                        filter = "filter1",
                        conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal,
                    ),
                    FilterWithFilteredNumberUIModel(
                        filter = "filter2",
                        conditionType = FilterCondition.FILTER_CONDITION_START.ordinal,
                    ),
                    FilterWithFilteredNumberUIModel(
                        filter = "filter3",
                        conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal,
                    ),
                    FilterWithFilteredNumberUIModel(
                        filter = "filter4",
                        conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal,
                    ),
                )
            val searchQuery = "filter"
            val filterIndexes =
                arrayListOf(
                    NumberDataFiltering.FILTER_CONDITION_FULL_FILTERING.ordinal,
                    NumberDataFiltering.FILTER_CONDITION_START_FILTERING.ordinal,
                )
            val expectedFilteredList =
                listOf(
                    FilterWithFilteredNumberUIModel(
                        filter = "filter1",
                        conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal,
                    ),
                    FilterWithFilteredNumberUIModel(
                        filter = "filter2",
                        conditionType = FilterCondition.FILTER_CONDITION_START.ordinal,
                    ),
                )
            viewModel.getFilteredFilterList(filterList, searchQuery, filterIndexes)
            advanceUntilIdle()
            assertEquals(expectedFilteredList, viewModel.filteredFilterListLiveData.getOrAwaitValue())
        }

    @Test
    fun deleteFilterListTest() =
        runTest {
            val expectedResult = Result.Success<Unit>()
            val filterList =
                listOf(
                    FilterWithFilteredNumber(filter = Filter(filter = TEST_FILTER)),
                    FilterWithFilteredNumber(filter = Filter(filter = "mockFilter2")),
                )
            val filterUIModelList =
                listOf(
                    FilterWithFilteredNumberUIModel(filter = TEST_FILTER),
                    FilterWithFilteredNumberUIModel(filter = "mockFilter2"),
                )
            val filterIdList = filterList.mapNotNull { it.filter }
            every { application.isNetworkAvailable } returns true
            every { filterWithFilteredNumberUIMapper.mapFromUIModelList(filterUIModelList) } returns filterList
            coEvery { useCase.deleteFilterList(eq(filterIdList), any(), any()) } answers {
                val result = thirdArg<(Result<Unit>) -> Unit>()
                result.invoke(expectedResult)
            }
            viewModel.deleteFilterList(filterUIModelList)
            advanceUntilIdle()
            coVerify { useCase.deleteFilterList(eq(filterIdList), any(), any()) }
            verify { filterWithFilteredNumberUIMapper.mapFromUIModelList(filterUIModelList) }
            assertEquals(true, viewModel.successDeleteFilterLiveData.getOrAwaitValue())
        }

    @Test
    fun getCurrentCountryCodeTest() =
        runTest {
            val countryCode = CountryCode()
            val countryCodeUIModel = CountryCodeUIModel()
            coEvery { useCase.getCurrentCountryCode() } returns flowOf(countryCode)
            coEvery { countryCodeUIMapper.mapToUIModel(countryCode) } returns countryCodeUIModel
            viewModel.getCurrentCountryCode()
            advanceUntilIdle()
            coVerify { useCase.getCurrentCountryCode() }
            assertEquals(countryCodeUIModel, viewModel.currentCountryCodeLiveData.getOrAwaitValue())
        }
}
