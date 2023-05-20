package com.tarasovvp.smartblocker.usecases

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumbers
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecases.ListFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListFilterUseCaseImpl
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ListFilterUseCaseTest {

    @MockK
    private lateinit var filterRepository: FilterRepository

    @MockK
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @MockK
    private lateinit var firebaseAuth: FirebaseAuth

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    private lateinit var listFilterUseCase: ListFilterUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        listFilterUseCase = ListFilterUseCaseImpl(filterRepository, realDataBaseRepository, firebaseAuth)
    }

    @Test
    fun allFilterWithFilteredNumbersByTypeTest() = runBlocking {
        val filterList = listOf(FilterWithFilteredNumbers(filter = Filter(filter = TEST_FILTER)), FilterWithFilteredNumbers(filter = Filter(filter = "mockFilter2")))
        coEvery { filterRepository.allFilterWithFilteredNumbersByType(BLOCKER) } returns filterList
        val result = listFilterUseCase.allFilterWithFilteredNumbersByType(isBlockerList = true)
        assertEquals(TEST_FILTER, result?.get(0)?.filter?.filter)
    }

    @Test
    fun getFilteredFilterListTest() = runBlocking {
        val filterList = listOf(
            FilterWithFilteredNumbers(Filter("filter1", conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal)),
            FilterWithFilteredNumbers(Filter("filter2", conditionType = FilterCondition.FILTER_CONDITION_START.ordinal)),
            FilterWithFilteredNumbers(Filter("filter3", conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal)),
            FilterWithFilteredNumbers(Filter("filter4", conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal))
        )
        val searchQuery = "filter"
        val filterIndexes = arrayListOf(
            NumberDataFiltering.FILTER_CONDITION_FULL_FILTERING.ordinal,
            NumberDataFiltering.FILTER_CONDITION_START_FILTERING.ordinal
        )
        val expectedFilteredList = listOf(
            FilterWithFilteredNumbers(Filter("filter1", FilterCondition.FILTER_CONDITION_FULL.ordinal)), FilterWithFilteredNumbers(Filter("filter2", FilterCondition.FILTER_CONDITION_START.ordinal)))
        val result = listFilterUseCase.getFilteredFilterList(filterList, searchQuery, filterIndexes)
        assertEquals(expectedFilteredList, result)
    }

    @Test
    fun deleteFilterListTest() = runBlocking {
        val filterList = listOf(Filter())
        every { firebaseAuth.currentUser } returns mockk()
        val expectedResult = Result.Success<Unit>()
        coEvery { realDataBaseRepository.deleteFilterList(eq(filterList), any()) } coAnswers {
            val callback = secondArg<(Result<Unit>) -> Unit>()
            callback.invoke(expectedResult)
        }
        coEvery { filterRepository.deleteFilterList(eq(filterList)) } just Runs

        listFilterUseCase.deleteFilterList(filterList, true, resultMock)

        verify { realDataBaseRepository.deleteFilterList(eq(filterList), any()) }
        coVerify { filterRepository.deleteFilterList(eq(filterList)) }
        verify { resultMock.invoke(expectedResult) }
    }
}