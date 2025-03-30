package com.tarasovvp.smartblocker.domain.repository

import androidx.datastore.preferences.core.Preferences
import com.tarasovvp.smartblocker.domain.entities.dbentities.CountryCode
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setOnBoardingSeen(isOnBoardingSeen: Boolean)

    suspend fun onBoardingSeen(): Flow<Boolean?>

    suspend fun setAppLang(appLang: String)

    suspend fun getAppLang(): Flow<String?>

    suspend fun setAppTheme(appTheme: Int)

    suspend fun getAppTheme(): Flow<Int?>

    suspend fun setBlockerTurnOn(blockerTurnOn: Boolean)

    suspend fun blockerTurnOn(): Flow<Boolean?>

    suspend fun setBlockHidden(blockHidden: Boolean)

    suspend fun blockHidden(): Flow<Boolean?>

    suspend fun setCountryCode(countryCode: CountryCode)

    suspend fun getCountryCode(): Flow<CountryCode?>

    suspend fun setReviewVoted(isReviewVoted: Boolean)

    suspend fun reviewVoted(): Flow<Boolean?>

    suspend fun clearDataByKeys(keys: List<Preferences.Key<*>>)
}
