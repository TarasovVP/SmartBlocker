package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_filter.ListFilterUseCaseImpl
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ListFilterUseCaseTest {

    @Mock
    private lateinit var filterRepository: FilterRepository

    private lateinit var listFilterUseCaseImpl: ListFilterUseCaseImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        listFilterUseCaseImpl = ListFilterUseCaseImpl(filterRepository)
    }

    suspend fun getFilterList(isBlackList: Boolean) = filterRepository.allFiltersByType(if (isBlackList) BLOCKER else PERMISSION)

    suspend fun getHashMapFromFilterList(filterList: List<FilterWithCountryCode>) = filterRepository.getHashMapFromFilterList(filterList)

    suspend fun deleteFilterList(filterList: List<Filter?>, result: () -> Unit) = filterRepository.deleteFilterList(filterList) {
        result.invoke()
    }
}