package com.tarasovvp.smartblocker.usecases

import com.nhaarman.mockitokotlin2.*
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_filter.ListFilterUseCase
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_filter.ListFilterUseCaseImpl
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

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
        Mockito.`when`(filterRepository.allFiltersByType(BLOCKER))
            .thenReturn(filterList)
        val result = listFilterUseCase.getFilterList(isBlackList = true)
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
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(realDataBaseRepository).deleteFilterList(eq(filterList), any())
        Mockito.`when`(filterRepository.deleteFilterList(eq(filterList))).thenReturn(Unit)
        listFilterUseCase.deleteFilterList(filterList, true, resultMock)
        verify(realDataBaseRepository).deleteFilterList(eq(filterList), any())
        verify(filterRepository).deleteFilterList(eq(filterList))
        verify(resultMock).invoke()
        listFilterUseCase.deleteFilterList(filterList, false, resultMock)
        verify(realDataBaseRepository, times(1)).deleteFilterList(eq(filterList), any())
        verify(filterRepository, times(2)).deleteFilterList(eq(filterList))
        verify(resultMock, times(2)).invoke()
    }
}