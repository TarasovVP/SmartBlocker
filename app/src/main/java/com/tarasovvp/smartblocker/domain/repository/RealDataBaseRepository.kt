package com.tarasovvp.smartblocker.domain.repository

import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall
import com.tarasovvp.smartblocker.domain.models.entities.CurrentUser
import com.tarasovvp.smartblocker.domain.models.Review

interface RealDataBaseRepository {

    fun getCurrentUser(result: (CurrentUser?) -> Unit)

    fun insertFilter(filter: Filter, result: () -> Unit)

    fun deleteFilterList(filterList: List<Filter?>, result: () -> Unit)

    fun insertFilteredCall(filteredCall: FilteredCall, result: () -> Unit)

    fun deleteFilteredCallList(filteredCallIdList: List<String>, result: () -> Unit)

    fun changeBlockHidden(blockUnanimous: Boolean, result: () -> Unit)

    fun insertReview(review: Review, result: () -> Unit)
}