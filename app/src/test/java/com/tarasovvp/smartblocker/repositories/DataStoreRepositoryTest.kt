package com.tarasovvp.smartblocker.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.tarasovvp.smartblocker.data.repositoryImpl.DataStoreRepositoryImpl
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.ON_BOARDING_SEEN
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DataStoreRepositoryTest {

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var dataStore: DataStore<Preferences>

    @MockK
    private lateinit var preferences: MutablePreferences

    private lateinit var dataStoreRepository: DataStoreRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dataStoreRepository = DataStoreRepositoryImpl(context)
    }

    @Test
    fun setOnBoardingSeenTest() = runBlocking {

    }

    @Test
    fun onBoardingSeenTest() = runBlocking {

    }
}