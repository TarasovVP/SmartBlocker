package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_call.ListCallUseCaseImpl
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ListCallUseCaseTest {

    @Mock
    private lateinit var logCallRepository: LogCallRepository

    @Mock
    private lateinit var filteredCallRepository: FilteredCallRepository

    private lateinit var listContactUseCaseImpl: ListCallUseCaseImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        listContactUseCaseImpl = ListCallUseCaseImpl(logCallRepository, filteredCallRepository)
    }

    suspend fun getCallList(): List<CallWithFilter> {
            val logCalls = logCallRepository.getAllLogCallWithFilter()
            val filteredCalls =  filteredCallRepository.allFilteredCallWithFilter()
            val filteredCallList = filteredCalls
            val logCallList = logCalls
            val callList = ArrayList<CallWithFilter>().apply {
                addAll(filteredCallList)
                addAll(logCallList)
            }
            return callList.distinctBy {
                it.call?.callId
            }
    }

    suspend fun getHashMapFromCallList(callList: List<CallWithFilter>) = logCallRepository.getHashMapFromCallList(callList.sortedByDescending {
        it.call?.callDate
    })

    suspend fun deleteCallList(filteredCallIdList: List<Int>, result: () -> Unit) = filteredCallRepository.deleteFilteredCalls(filteredCallIdList) {
        result.invoke()
    }
}
