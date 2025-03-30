package com.tarasovvp.smartblocker.viewmodels

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.getOrAwaitValue
import com.tarasovvp.smartblocker.domain.entities.dbentities.Filter
import com.tarasovvp.smartblocker.domain.entities.dbentities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.dbviews.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.dbviews.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.domain.usecases.DetailsFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.details.detailsfilter.DetailsFilterViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.ContactWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.uimodels.CallWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithFilteredNumberUIModel
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
class DetailsFilterViewModelUnitTest : BaseViewModelUnitTest<DetailsFilterViewModel>() {
    @MockK
    private lateinit var useCase: DetailsFilterUseCase

    @MockK
    private lateinit var filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper

    @MockK
    private lateinit var callWithFilterUIMapper: CallWithFilterUIMapper

    @MockK
    private lateinit var contactWithFilterUIMapper: ContactWithFilterUIMapper

    override fun createViewModel() =
        DetailsFilterViewModel(
            application,
            useCase,
            filterWithFilteredNumberUIMapper,
            callWithFilterUIMapper,
            contactWithFilterUIMapper,
        )

    @Test
    fun getQueryContactCallListTest() =
        runTest {
        /*val filter = Filter(filter = TEST_FILTER)
        val numberDataList = arrayListOf(ContactWithFilter(contact = Contact(number = TEST_NUMBER)), CallWithFilter().apply { call = Call(number = TEST_FILTER) })
        coEvery { useCase.numberDataListByFilter(filter) } returns numberDataList
        viewModel.getQueryContactCallList(filter)
        advanceUntilIdle()
        val result = viewModel.numberDataListLiveDataUIModel.getOrAwaitValue()
        assertEquals(TEST_NUMBER, (result[0] as ContactWithFilter).contact?.number)*/
        }

    @Test
    fun filteredCallsByFilterTest() =
        runTest {
            val filteredCallList =
                listOf(
                    CallWithFilter().apply {
                        call = FilteredCall().apply { this.number = TEST_NUMBER }
                    },
                )
            val filteredCallUIModelList = listOf(CallWithFilterUIModel(number = TEST_NUMBER))
            coEvery { useCase.allFilteredCallsByFilter(TEST_FILTER) } returns filteredCallList
            every { callWithFilterUIMapper.mapToUIModelList(filteredCallList) } returns filteredCallUIModelList
            viewModel.filteredCallsByFilter(TEST_FILTER)
            advanceUntilIdle()
            coVerify { useCase.allFilteredCallsByFilter(TEST_FILTER) }
            verify { callWithFilterUIMapper.mapToUIModelList(filteredCallList) }
            assertEquals(filteredCallUIModelList, viewModel.filteredCallListLiveData.getOrAwaitValue())
        }

    @Test
    fun updateFilterTest() =
        runTest {
            val expectedResult = Result.Success<Unit>()
            val filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(filter = TEST_FILTER)
            val filterWithFilteredNumber =
                FilterWithFilteredNumber(filter = Filter(filter = TEST_FILTER))
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
            val filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(filter = TEST_FILTER)
            val filterWithFilteredNumber =
                FilterWithFilteredNumber(filter = Filter(filter = TEST_FILTER))
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
