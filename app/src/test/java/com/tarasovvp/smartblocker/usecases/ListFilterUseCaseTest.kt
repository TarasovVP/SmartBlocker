package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecases.ListFilterUseCase
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListFilterUseCaseImpl
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
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

    @MockK(relaxed = true)
    private lateinit var resultMock: () -> Unit

    private lateinit var listFilterUseCase: ListFilterUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        listFilterUseCase = ListFilterUseCaseImpl(filterRepository, realDataBaseRepository)
    }

    @Test
    fun getFilterListTest() = runBlocking {
        val filterList = listOf(FilterWithCountryCode(filter = Filter(filter = TEST_FILTER)), FilterWithCountryCode(filter = Filter(filter = "mockFilter2")))
        coEvery { filterRepository.allFilterWithCountryCodesByType(BLOCKER) } returns filterList
        val result = listFilterUseCase.allFilterWithCountryCodesByType(isBlackList = true)
        assertEquals(TEST_FILTER, result?.get(0)?.filter?.filter)
    }

    @Test
    fun getHashMapFromFilterListTest() = runBlocking {
        val filterList = listOf(FilterWithCountryCode(filter = Filter(filter = TEST_FILTER)), FilterWithCountryCode(filter = Filter(filter = "mockFilter2")))
        val filterMap = mapOf(String.EMPTY to filterList)
        val result = listFilterUseCase.getHashMapFromFilterList(filterList)
        assertEquals(filterMap, result)
    }

    @Test
    fun deleteFilterListTest() = runBlocking {
        val filterList = listOf(Filter())
        every { realDataBaseRepository.deleteFilterList(eq(filterList), any()) } coAnswers {
            val callback = secondArg<() -> Unit>()
            callback.invoke()
        }
        coEvery { filterRepository.deleteFilterList(eq(filterList)) } just Runs

        listFilterUseCase.deleteFilterList(filterList, true, resultMock)

        verify { realDataBaseRepository.deleteFilterList(eq(filterList), any()) }
        coVerify { filterRepository.deleteFilterList(eq(filterList)) }
        verify { resultMock.invoke() }

        listFilterUseCase.deleteFilterList(filterList, false, resultMock)

        verify(exactly = 1) { realDataBaseRepository.deleteFilterList(eq(filterList), any()) }
        coVerify(exactly = 2) { filterRepository.deleteFilterList(eq(filterList)) }
        verify(exactly = 2) { resultMock.invoke() }
    }
}