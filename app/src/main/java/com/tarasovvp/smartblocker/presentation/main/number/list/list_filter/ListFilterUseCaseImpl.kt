package com.tarasovvp.smartblocker.presentation.main.number.list.list_filter

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.ListFilterUseCase
import com.tarasovvp.smartblocker.utils.extensions.isContaining
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListFilterUseCaseImpl @Inject constructor(
    private val filterRepository: FilterRepository,
    private val realDataBaseRepository: RealDataBaseRepository,
    private val firebaseAuth: FirebaseAuth
): ListFilterUseCase {

    override suspend fun allFilterWithCountryCodesByType(isBlockerList: Boolean) = filterRepository.allFilterWithCountryCodesByType(if (isBlockerList) BLOCKER else PERMISSION)

    override suspend fun getFilteredFilterList(
        filterList: List<FilterWithCountryCode>,
        searchQuery: String,
        filterIndexes: ArrayList<Int>
    ) = withContext(Dispatchers.Default) {
        if (searchQuery.isBlank() && filterIndexes.isEmpty()) filterList else filterList.filter { filterWithCountryCode ->
            (filterWithCountryCode.filter?.filter isContaining  searchQuery)
                    && (filterIndexes.contains(NumberDataFiltering.FILTER_CONDITION_FULL_FILTERING.ordinal) && filterWithCountryCode.filter?.filterType == FilterCondition.FILTER_CONDITION_FULL.ordinal
                    || filterIndexes.contains(NumberDataFiltering.FILTER_CONDITION_START_FILTERING.ordinal) && filterWithCountryCode.filter?.filterType == FilterCondition.FILTER_CONDITION_START.ordinal
                    || filterIndexes.contains(NumberDataFiltering.FILTER_CONDITION_CONTAIN_FILTERING.ordinal) && filterWithCountryCode.filter?.filterType == FilterCondition.FILTER_CONDITION_CONTAIN.ordinal
                    || filterIndexes.isEmpty())
        }
    }

    override suspend fun deleteFilterList(filterList: List<Filter>, isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit) {
        if (firebaseAuth.currentUser.isNotNull()) {
            if (isNetworkAvailable) {
                realDataBaseRepository.deleteFilterList(filterList) {
                    runBlocking {
                        filterRepository.deleteFilterList(filterList)
                        result.invoke(Result.Success())
                    }
                }
            } else {
                result.invoke(Result.Failure())
            }
        } else {
            filterRepository.deleteFilterList(filterList)
            result.invoke(Result.Success())
        }
    }
}