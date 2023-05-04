package com.tarasovvp.smartblocker.presentation.main.number.list.list_filter

import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult
import com.tarasovvp.smartblocker.domain.usecase.ListFilterUseCase
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isContaining
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ListFilterUseCaseImpl @Inject constructor(
    private val filterRepository: FilterRepository,
    private val realDataBaseRepository: RealDataBaseRepository
): ListFilterUseCase {

    override suspend fun getFilterList(isBlackList: Boolean) = filterRepository.allFiltersByType(if (isBlackList) BLOCKER else PERMISSION)

    override suspend fun getFilteredFilterList(
        filterList: List<FilterWithCountryCode>,
        searchQuery: String,
        filterIndexes: ArrayList<Int>
    ): List<FilterWithCountryCode> {
        return if (searchQuery.isBlank() && filterIndexes.isEmpty()) filterList else filterList.filter { filterWithCountryCode ->
            (filterWithCountryCode.filter?.filter isContaining  searchQuery)
                    && (filterIndexes.contains(NumberDataFiltering.FILTER_CONDITION_FULL_FILTERING.ordinal) && filterWithCountryCode.filter?.isTypeFull().isTrue()
                    || filterIndexes.contains(NumberDataFiltering.FILTER_CONDITION_START_FILTERING.ordinal) && filterWithCountryCode.filter?.isTypeStart().isTrue()
                    || filterIndexes.contains(NumberDataFiltering.FILTER_CONDITION_CONTAIN_FILTERING.ordinal) && filterWithCountryCode.filter?.isTypeContain().isTrue()
                    || filterIndexes.isEmpty())
        }
    }

    override suspend fun getHashMapFromFilterList(filterList: List<FilterWithCountryCode>): Map<String, List<FilterWithCountryCode>> {
        return filterList.groupBy { String.EMPTY }
    }

    override suspend fun deleteFilterList(filterList: List<Filter?>, isLoggedInUser: Boolean, result: (OperationResult<Unit>) -> Unit) {
        if (isLoggedInUser) {
            realDataBaseRepository.deleteFilterList(filterList) {
                runBlocking {
                    filterRepository.deleteFilterList(filterList)
                    result.invoke()
                }
            }
        } else {
            filterRepository.deleteFilterList(filterList)
            result.invoke()
        }
    }
}