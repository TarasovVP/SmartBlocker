package com.tarasovvp.smartblocker.data.repositoryImpl

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_THEME
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_TURN_OFF
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.ON_BOARDING_SEEN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take

class DataStoreRepositoryImpl(private val context: Context) : DataStoreRepository {

    private val Context.dataStore by preferencesDataStore(context.packageName)

    override suspend fun setOnBoardingSeen(isOnBoardingSeen: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(ON_BOARDING_SEEN)] = isOnBoardingSeen
        }
    }

    override suspend fun onBoardingSeen(): Flow<Boolean?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[booleanPreferencesKey(ON_BOARDING_SEEN)]
            }.take(1)
    }

    override suspend fun setAppLang(appLang: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(APP_LANG)] = appLang
        }
    }

    override suspend fun getAppLang(): Flow<String?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[stringPreferencesKey(APP_LANG)]
            }.take(1)
    }

    override suspend fun setAppTheme(appTheme: Int) {
        context.dataStore.edit { preferences ->
            preferences[intPreferencesKey(APP_THEME)] = appTheme
        }
    }

    override suspend fun getAppTheme(): Flow<Int?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[intPreferencesKey(APP_THEME)]
            }.take(1)
    }

    override suspend fun setBlockerTurnOff(smartBlockerTurnOff: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(BLOCK_TURN_OFF)] = smartBlockerTurnOff
        }
    }

    override suspend fun blockerTurnOff(): Flow<Boolean?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[booleanPreferencesKey(BLOCK_TURN_OFF)]
            }.take(1)
    }

    override suspend fun setBlockHidden(smartBlockerTurnOff: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(BLOCK_HIDDEN)] = smartBlockerTurnOff
        }
    }

    override suspend fun blockHidden(): Flow<Boolean?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[booleanPreferencesKey(BLOCK_HIDDEN)]
            }.take(1)
    }


    override suspend fun setCountryCode(countryCode: CountryCode) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(COUNTRY)] = Gson().toJson(countryCode)
        }
    }

    override suspend fun getCountryCode(): Flow<CountryCode?> {
        return context.dataStore.data
            .map { preferences ->
                try {
                    Gson().fromJson(preferences[stringPreferencesKey(COUNTRY)],  CountryCode::class.java)
                } catch (e: java.lang.Exception) {
                    CountryCode()
                }
            }.take(1)
    }
}