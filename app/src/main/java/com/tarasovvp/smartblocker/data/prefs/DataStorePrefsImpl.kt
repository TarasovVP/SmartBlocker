package com.tarasovvp.smartblocker.data.prefs

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_THEME
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_TURN_OFF
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.ON_BOARDING_SEEN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStorePrefsImpl(private val context: Context) : DataStorePrefs {

    private val Context.dataStore by preferencesDataStore(context.packageName)

    override suspend fun saveIsOnBoardingSeen(isOnBoardingSeen: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(ON_BOARDING_SEEN)] = isOnBoardingSeen
        }
    }

    override suspend fun isOnBoardingSeen(): Flow<Boolean?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[booleanPreferencesKey(ON_BOARDING_SEEN)]
            }
    }

    override suspend fun saveAppLang(appLang: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(APP_LANG)] = appLang
        }
    }

    override suspend fun getAppLang(): Flow<String?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[stringPreferencesKey(APP_LANG)]
            }
    }

    override suspend fun saveAppTheme(appTheme: Int) {
        context.dataStore.edit { preferences ->
            preferences[intPreferencesKey(APP_THEME)] = appTheme
        }
    }

    override suspend fun getAppTheme(): Flow<Int?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[intPreferencesKey(APP_THEME)]
            }
    }

    override suspend fun saveIsSmartBlockerTurnOff(smartBlockerTurnOff: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(BLOCK_TURN_OFF)] = smartBlockerTurnOff
        }
    }

    override suspend fun isSmartBlockerTurnOff(): Flow<Boolean?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[booleanPreferencesKey(BLOCK_TURN_OFF)]
            }
    }

    override suspend fun saveIsBlockHidden(smartBlockerTurnOff: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(BLOCK_HIDDEN)] = smartBlockerTurnOff
        }
    }

    override suspend fun isBlockHidden(): Flow<Boolean?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[booleanPreferencesKey(BLOCK_HIDDEN)]
            }
    }


    override suspend fun saveCountry(country: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(COUNTRY)] = country
        }
    }

    override suspend fun getCountry(): Flow<String?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[stringPreferencesKey(COUNTRY)]
            }
    }

    override suspend fun saveTest(country: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(COUNTRY)] = country
        }
    }
}