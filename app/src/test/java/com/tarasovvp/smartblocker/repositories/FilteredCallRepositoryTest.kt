package com.tarasovvp.smartblocker.repositories

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.TestUtils
import com.tarasovvp.smartblocker.TestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.data.database.dao.FilteredCallDao
import com.tarasovvp.smartblocker.data.repositoryImpl.FilteredCallRepositoryImpl
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FilteredCallRepositoryTest {

    @Mock
    private lateinit var filteredCallDao: FilteredCallDao

    @Mock
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    private lateinit var filteredCallRepository: FilteredCallRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        filteredCallRepository = FilteredCallRepositoryImpl(filteredCallDao, realDataBaseRepository)
    }

    @Test
    fun setFilterToFilteredCallTest() = runTest {

    }

    @Test
    fun insertAllFilteredCallsTest() = runTest {
        val filteredCallList = listOf(FilteredCall().apply { number = TEST_NUMBER }, FilteredCall())
        filteredCallRepository.insertAllFilteredCalls(filteredCallList)
        verify(filteredCallDao, times(1)).insertAllFilteredCalls(filteredCallList)
    }

    @Test
    fun insertFilteredCallTest() = runTest {
        val filteredCall = FilteredCall().apply { number = TEST_NUMBER }
        filteredCallRepository.insertFilteredCall(filteredCall)
        verify(filteredCallDao, times(1)).insertFilteredCall(filteredCall)
    }

    @Test
    fun allFilteredCallsTest() = runTest {

    }

    @Test
    fun allFilteredCallWithFilterTest() = runTest {

    }

    @Test
    fun filteredCallsByFilterTest() = runTest {

    }

    @Test
    fun filteredCallsByNumberTest() = runTest {

    }

    @Test
    fun deleteFilteredCallsTest() = runTest {
        val resultMock = mock<() -> Unit>()
        val filteredCallIdList = listOf(1, 2)
        filteredCallRepository.deleteFilteredCalls(filteredCallIdList, resultMock)
        verify(filteredCallDao).deleteFilteredCalls(filteredCallIdList)
        verify(resultMock).invoke()
    }

}