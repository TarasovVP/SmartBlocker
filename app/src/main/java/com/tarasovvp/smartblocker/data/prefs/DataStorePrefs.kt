package com.tarasovvp.smartblocker.data.prefs

import kotlinx.coroutines.flow.Flow

interface DataStorePrefs {

    suspend fun saveIsOnBoardingSeen(isOnBoardingSeen: Boolean)

    suspend fun isOnBoardingSeen(): Flow<Boolean?>

    suspend fun saveAppLang(appLang: String)

    suspend fun getAppLang(): Flow<String?>

    suspend fun saveAppTheme(appTheme: Int)

    suspend fun getAppTheme(): Flow<Int?>

    suspend fun saveIsSmartBlockerTurnOff(smartBlockerTurnOff: Boolean)

    suspend fun isSmartBlockerTurnOff(): Flow<Boolean?>

    suspend fun saveIsBlockHidden(smartBlockerTurnOff: Boolean)

    suspend fun isBlockHidden(): Flow<Boolean?>

    suspend fun saveCountry(country: String)

    suspend fun getCountry(): Flow<String?>

    suspend fun saveTest(country: String)
}