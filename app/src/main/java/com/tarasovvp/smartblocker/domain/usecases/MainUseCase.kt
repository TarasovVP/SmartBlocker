package com.tarasovvp.smartblocker.domain.usecases

import android.app.Application
import com.tarasovvp.smartblocker.domain.entities.dbentities.Contact
import com.tarasovvp.smartblocker.domain.entities.dbentities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.dbentities.Filter
import com.tarasovvp.smartblocker.domain.entities.dbentities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.dbentities.LogCall
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import kotlinx.coroutines.flow.Flow

interface MainUseCase {
    suspend fun getOnBoardingSeen(): Flow<Boolean?>

    suspend fun getBlockerTurnOn(): Flow<Boolean?>

    suspend fun setBlockerTurnOn(blockTurnOn: Boolean)

    suspend fun setBlockHidden(blockHidden: Boolean)

    fun getCurrentUser(result: (Result<CurrentUser>) -> Unit)

    suspend fun setReviewVoted(isReviewVoted: Boolean)

    suspend fun getSystemCountryCodes(result: (Int, Int) -> Unit): List<CountryCode>

    suspend fun getCurrentCountryCode(): Flow<CountryCode?>

    suspend fun setCurrentCountryCode(countryCode: CountryCode)

    suspend fun insertAllCountryCodes(countryCodeList: List<CountryCode>)

    suspend fun getAllFilters(): List<Filter>

    suspend fun getSystemContacts(
        application: Application,
        result: (Int, Int) -> Unit,
    ): List<Contact>

    suspend fun insertAllContacts(contactList: List<Contact>)

    suspend fun getSystemLogCalls(
        application: Application,
        result: (Int, Int) -> Unit,
    ): List<LogCall>

    suspend fun insertAllLogCalls(logCallList: List<LogCall>)

    suspend fun insertAllFilteredCalls(filteredCallList: List<FilteredCall>)

    suspend fun insertAllFilters(filterList: List<Filter>)
}
