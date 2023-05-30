package com.tarasovvp.smartblocker.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.google.gson.Gson
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_APP_LANGUAGE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_APP_THEME
import com.tarasovvp.smartblocker.data.repositoryImpl.DataStoreRepositoryImpl
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_THEME
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_TURN_ON
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.ON_BOARDING_SEEN
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DataStoreRepositoryUnitTest {

    @MockK
    private lateinit var dataStore: DataStore<Preferences>

    private lateinit var dataStoreRepository: DataStoreRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dataStoreRepository = DataStoreRepositoryImpl(dataStore)
    }

    @Test
    fun setOnBoardingSeenTest() = runBlocking {
        val isOnBoardingSeen = false
        val key = booleanPreferencesKey(ON_BOARDING_SEEN)
        val preferences = preferencesOf(key to isOnBoardingSeen)
        coEvery { dataStore.updateData(any()) } returns flowOf(preferences).first()
        coEvery { dataStore.data } returns flowOf(preferences)
        dataStoreRepository.setOnBoardingSeen(isOnBoardingSeen)
        assertEquals(isOnBoardingSeen, dataStore.data.first()[booleanPreferencesKey(ON_BOARDING_SEEN)])
    }

    @Test
    fun onBoardingSeenTest() = runBlocking {
        val isOnBoardingSeen = true
        val key = booleanPreferencesKey(ON_BOARDING_SEEN)
        val preferences = preferencesOf(key to isOnBoardingSeen)
        coEvery { dataStore.data } returns flowOf(preferences)
        val result = dataStoreRepository.onBoardingSeen().first()
        assertEquals(isOnBoardingSeen, result)
    }

    @Test
    fun setAppLangTest() = runBlocking {
        val appLang = TEST_APP_LANGUAGE
        val key = stringPreferencesKey(APP_LANG)
        val preferences = preferencesOf(key to appLang)
        coEvery { dataStore.updateData(any()) } returns flowOf(preferences).first()
        coEvery { dataStore.data } returns flowOf(preferences)
        dataStoreRepository.setAppLang(appLang)
        assertEquals(appLang, dataStore.data.first()[stringPreferencesKey(APP_LANG)])
    }

    @Test
    fun getAppLangTest() = runBlocking {
        val appLang = TEST_APP_LANGUAGE
        val key = stringPreferencesKey(APP_LANG)
        val preferences = preferencesOf(key to appLang)
        coEvery { dataStore.data } returns flowOf(preferences)
        val result = dataStoreRepository.getAppLang().first()
        assertEquals(appLang, result)
    }

    @Test
    fun setAppThemeTest() = runBlocking {
        val appTheme = TEST_APP_THEME
        val key = intPreferencesKey(APP_THEME)
        val preferences = preferencesOf(key to appTheme)
        coEvery { dataStore.updateData(any()) } returns flowOf(preferences).first()
        coEvery { dataStore.data } returns flowOf(preferences)
        dataStoreRepository.setAppTheme(appTheme)
        assertEquals(appTheme, dataStore.data.first()[intPreferencesKey(APP_THEME)])
    }

    @Test
    fun getAppThemeTest() = runBlocking {
        val appTheme = TEST_APP_THEME
        val key = intPreferencesKey(APP_THEME)
        val preferences = preferencesOf(key to appTheme)
        coEvery { dataStore.data } returns flowOf(preferences)
        val result = dataStoreRepository.getAppTheme().first()
        assertEquals(appTheme, result)
    }

    @Test
    fun setBlockerTurnOffTest() = runBlocking {
        val blockerTurnOff = true
        val key = booleanPreferencesKey(BLOCK_TURN_ON)
        val preferences = preferencesOf(key to blockerTurnOff)
        coEvery { dataStore.updateData(any()) } returns flowOf(preferences).first()
        coEvery { dataStore.data } returns flowOf(preferences)
        dataStoreRepository.setBlockerTurnOn(blockerTurnOff)
        assertEquals(blockerTurnOff, dataStore.data.first()[booleanPreferencesKey(BLOCK_TURN_ON)])
    }

    @Test
    fun blockerTurnOffTest() = runBlocking {
        val blockerTurnOff = true
        val key = booleanPreferencesKey(BLOCK_TURN_ON)
        val preferences = preferencesOf(key to blockerTurnOff)
        coEvery { dataStore.data } returns flowOf(preferences)
        val result = dataStoreRepository.blockerTurnOn().first()
        assertEquals(blockerTurnOff, result)
    }

    @Test
    fun setBlockHiddenTest() = runBlocking {
        val blockHidden = false
        val key = booleanPreferencesKey(BLOCK_HIDDEN)
        val preferences = preferencesOf(key to blockHidden)
        coEvery { dataStore.updateData(any()) } returns flowOf(preferences).first()
        coEvery { dataStore.data } returns flowOf(preferences)
        dataStoreRepository.setOnBoardingSeen(blockHidden)
        assertEquals(blockHidden, dataStore.data.first()[booleanPreferencesKey(BLOCK_HIDDEN)])
    }

    @Test
    fun blockHiddenTest() = runBlocking {
        val blockHidden = false
        val key = booleanPreferencesKey(BLOCK_HIDDEN)
        val preferences = preferencesOf(key to blockHidden)
        coEvery { dataStore.data } returns flowOf(preferences)
        val result = dataStoreRepository.blockHidden().first()
        assertEquals(blockHidden, result)
    }

    @Test
    fun setCountryCodeTest() = runBlocking {
        val countryCode = CountryCode()
        val key = stringPreferencesKey(COUNTRY_CODE)
        val preferences = preferencesOf(key to Gson().toJson(CountryCode()))
        coEvery { dataStore.updateData(any()) } returns flowOf(preferences).first()
        coEvery { dataStore.data } returns flowOf(preferences)
        dataStoreRepository.setCountryCode(countryCode)
        assertEquals(Gson().toJson(CountryCode()), dataStore.data.first()[stringPreferencesKey(COUNTRY_CODE)])
    }

    @Test
    fun getCountryCodeTest() = runBlocking {
        val countryCode = CountryCode()
        val key = stringPreferencesKey(COUNTRY_CODE)
        val preferences = preferencesOf(key to Gson().toJson(CountryCode()))
        coEvery { dataStore.data } returns flowOf(preferences)
        val result = dataStoreRepository.getCountryCode().first()
        assertEquals(countryCode, result)
    }
}