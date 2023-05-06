package com.tarasovvp.smartblocker.domain.repository

import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall
import com.tarasovvp.smartblocker.domain.models.entities.CurrentUser
import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface RealDataBaseRepository {

    fun getCurrentUser(result: (Result<CurrentUser>) -> Unit)

    fun insertFilter(filter: Filter, result: (Result<Unit>) -> Unit)

    fun deleteFilterList(filterList: List<Filter?>, result: (Result<Unit>) -> Unit)

    fun insertFilteredCall(filteredCall: FilteredCall, result: (Result<Unit>) -> Unit)

    fun deleteFilteredCallList(filteredCallIdList: List<String>, result: (Result<Unit>) -> Unit)

    fun changeBlockHidden(blockUnanimous: Boolean, result: (Result<Unit>) -> Unit)

    fun insertReview(review: Review, result: (Result<Unit>) -> Unit)
}