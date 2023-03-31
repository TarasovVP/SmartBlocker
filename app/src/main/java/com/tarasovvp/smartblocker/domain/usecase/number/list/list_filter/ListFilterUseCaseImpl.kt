package com.tarasovvp.smartblocker.domain.usecase.number.list.list_filter

import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import javax.inject.Inject

class ListFilterUseCaseImpl @Inject constructor(
    private val filterRepository: FilterRepository
): ListFilterUseCase {

    override suspend fun getFilterList(isBlackList: Boolean) = filterRepository.allFiltersByType(if (isBlackList) BLOCKER else PERMISSION)

    override suspend fun getHashMapFromFilterList(filterList: List<FilterWithCountryCode>): Map<String, List<FilterWithCountryCode>> {
        return filterList.groupBy { String.EMPTY }
    }

    override suspend fun deleteFilterList(filterList: List<Filter?>, result: () -> Unit) = filterRepository.deleteFilterList(filterList) {
        result.invoke()
    }
}