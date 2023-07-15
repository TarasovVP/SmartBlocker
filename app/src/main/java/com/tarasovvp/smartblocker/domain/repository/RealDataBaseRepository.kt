package com.tarasovvp.smartblocker.domain.repository

import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.entities.models.Review
import com.tarasovvp.smartblocker.domain.sealed_classes.Result

interface RealDataBaseRepository {

    fun createCurrentUser(currentUser: CurrentUser, result: (Result<Unit>) -> Unit)

    fun updateCurrentUser(currentUser: CurrentUser, result: (Result<Unit>) -> Unit)

    fun getCurrentUser(result: (Result<CurrentUser>) -> Unit)

    fun deleteCurrentUser(result: (Result<Unit>) -> Unit)

    fun insertFilter(filter: Filter, result: (Result<Unit>) -> Unit)

    fun deleteFilterList(filterList: List<Filter?>, result: (Result<Unit>) -> Unit)

    fun insertFilteredCall(filteredCall: FilteredCall, result: (Result<Unit>) -> Unit)

    fun deleteFilteredCallList(filteredCallIdList: List<String>, result: (Result<Unit>) -> Unit)

    fun changeBlockTurnOn(blockTurnOn: Boolean, result: (Result<Unit>) -> Unit)

    fun changeBlockHidden(blockHidden: Boolean, result: (Result<Unit>) -> Unit)

    fun changeCountryCode(countryCode: CountryCode, result: (Result<Unit>) -> Unit)

    fun insertReview(review: Review, result: (Result<Unit>) -> Unit)

    fun getPrivacyPolicy(appLang: String, result: (Result<String>) -> Unit)
}