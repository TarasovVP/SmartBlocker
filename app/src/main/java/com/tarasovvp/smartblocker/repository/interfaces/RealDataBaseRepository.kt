package com.tarasovvp.smartblocker.repository.interfaces

import com.tarasovvp.smartblocker.models.*

interface RealDataBaseRepository {

    fun getCurrentUser(result: (CurrentUser?) -> Unit)

    fun insertFilter(filter: Filter, result: () -> Unit)

    fun deleteFilterList(filterList: List<Filter>, result: () -> Unit)

    fun insertFilteredCall(filteredCall: FilteredCall, result: () -> Unit)

    fun deleteFilteredCallList(filteredCallList: List<Call>, result: () -> Unit)

    fun changeBlockHidden(blockUnanimous: Boolean, result: () -> Unit)

    fun insertReview(review: Review, result: () -> Unit)
}