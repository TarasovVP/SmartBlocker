package com.tarasovvp.smartblocker.infrastructure

import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.receivers.CallReceiver
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class CallReceiverUnitTest {

    @MockK
    private lateinit var filterRepository: FilterRepository

    @MockK
    private lateinit var filteredCallRepository: FilteredCallRepository

    @MockK
    private lateinit var dataStoreRepository: DataStoreRepository

    private lateinit var callReceiver: CallReceiver

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        callReceiver = CallReceiver()
        callReceiver.filterRepository = filterRepository
        callReceiver.filteredCallRepository = filteredCallRepository
        callReceiver.dataStoreRepository = dataStoreRepository
    }

    @Test
    fun matchedFilterTest() {
        val number = "123456789"
        val isBlockHidden = true
        val expectedFilter = Filter(filterType = BLOCKER)

        coEvery { filterRepository.allFilterWithFilteredNumbersByNumber(number) } returns listOf(
            FilterWithFilteredNumber(filter = expectedFilter)
        )

        val result = runBlocking { callReceiver.matchedFilter(number, isBlockHidden) }

        coVerify { filterRepository.allFilterWithFilteredNumbersByNumber(number) }
        confirmVerified(filterRepository)

        assert(result == expectedFilter)
    }
}