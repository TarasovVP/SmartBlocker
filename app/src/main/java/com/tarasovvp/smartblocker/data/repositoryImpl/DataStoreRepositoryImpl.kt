package com.tarasovvp.smartblocker.data.repositoryImpl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.google.gson.Gson
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_THEME
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_TURN_ON
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.ON_BOARDING_SEEN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take

class DataStoreRepositoryImpl(private val dataStore: DataStore<Preferences>) : DataStoreRepository {

    override suspend fun setOnBoardingSeen(isOnBoardingSeen: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(ON_BOARDING_SEEN)] = isOnBoardingSeen
        }
    }

    override suspend fun onBoardingSeen(): Flow<Boolean?> {
        return dataStore.data
            .map { preferences ->
                preferences[booleanPreferencesKey(ON_BOARDING_SEEN)]
            }.take(1)
    }

    override suspend fun setAppLang(appLang: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(APP_LANG)] = appLang
        }
    }

    override suspend fun getAppLang(): Flow<String?> {
        return dataStore.data
            .map { preferences ->
                preferences[stringPreferencesKey(APP_LANG)]
            }.take(1)
    }

    override suspend fun setAppTheme(appTheme: Int) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey(APP_THEME)] = appTheme
        }
    }

    override suspend fun getAppTheme(): Flow<Int?> {
        return dataStore.data
            .map { preferences ->
                preferences[intPreferencesKey(APP_THEME)]
            }.take(1)
    }

    override suspend fun setBlockerTurnOn(blockerTurnOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(BLOCK_TURN_ON)] = blockerTurnOn
        }
    }

    override suspend fun blockerTurnOn(): Flow<Boolean?> {
        return dataStore.data
            .map { preferences ->
                preferences[booleanPreferencesKey(BLOCK_TURN_ON)]
            }.take(1)
    }

    override suspend fun setBlockHidden(blockHidden: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(BLOCK_HIDDEN)] = blockHidden
        }
    }

    override suspend fun blockHidden(): Flow<Boolean?> {
        return dataStore.data
            .map { preferences ->
                preferences[booleanPreferencesKey(BLOCK_HIDDEN)]
            }.take(1)
    }


    override suspend fun setCountryCode(countryCode: CountryCode) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(COUNTRY_CODE)] = Gson().toJson(countryCode)
        }
    }

    override suspend fun getCountryCode(): Flow<CountryCode?> {
        return dataStore.data
            .map { preferences ->
                try {
                    Gson().fromJson(preferences[stringPreferencesKey(COUNTRY_CODE)],  CountryCode::class.java)
                } catch (e: java.lang.Exception) {
                    CountryCode()
                }
            }.take(1)
    }
}