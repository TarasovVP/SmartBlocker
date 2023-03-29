package com.tarasovvp.smartblocker.repositories

import com.tarasovvp.smartblocker.data.database.dao.CountryCodeDao
import com.tarasovvp.smartblocker.data.repositoryImpl.CountryCodeRepositoryImpl
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CountryCodeRepositoryTest {

    @Mock
    private lateinit var countryCodeDao: CountryCodeDao

    private lateinit var countryCodeRepository: CountryCodeRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        countryCodeRepository = CountryCodeRepositoryImpl(countryCodeDao)
    }

    @Test
    suspend fun getSystemCountryCodeListTest() {

    }

    @Test
    suspend fun insertAllCountryCodesTest() {

    }

    @Test
    suspend fun getAllCountryCodesTest() {

    }

    @Test
    suspend fun getCountryCodeWithCountryTest() {

    }

    @Test
    suspend fun getCountryCodeWithCodeTest() {

    }
}