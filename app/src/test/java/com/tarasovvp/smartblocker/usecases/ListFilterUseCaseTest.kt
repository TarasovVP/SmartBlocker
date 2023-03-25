package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class ListFilterUseCaseTest @Inject constructor(
    private val filterRepository: FilterRepository
) {

    suspend fun getFilterList(isBlackList: Boolean) = filterRepository.allFiltersByType(if (isBlackList) BLOCKER else PERMISSION)

    suspend fun getHashMapFromFilterList(filterList: List<FilterWithCountryCode>) = filterRepository.getHashMapFromFilterList(filterList)

    suspend fun deleteFilterList(filterList: List<Filter?>, result: () -> Unit) = filterRepository.deleteFilterList(filterList) {
        result.invoke()
    }
}