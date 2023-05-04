package com.tarasovvp.smartblocker.domain.repository

import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall
import com.tarasovvp.smartblocker.domain.models.entities.CurrentUser
import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.domain.sealed_classes.OperationResult

interface RealDataBaseRepository {

    fun getCurrentUser(result: (OperationResult<CurrentUser>) -> Unit)

    fun insertFilter(filter: Filter, result: (OperationResult<Unit>) -> Unit)

    fun deleteFilterList(filterList: List<Filter?>, result: (OperationResult<Unit>) -> Unit)

    fun insertFilteredCall(filteredCall: FilteredCall, result: (OperationResult<Unit>) -> Unit)

    fun deleteFilteredCallList(filteredCallIdList: List<String>, result: (OperationResult<Unit>) -> Unit)

    fun changeBlockHidden(blockUnanimous: Boolean, result: (OperationResult<Unit>) -> Unit)

    fun insertReview(review: Review, result: (OperationResult<Unit>) -> Unit)
}