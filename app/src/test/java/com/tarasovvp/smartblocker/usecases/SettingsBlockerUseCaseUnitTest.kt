package com.tarasovvp.smartblocker.usecases

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.entities.dbentities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settingsblocker.SettingsBlockerUseCaseImpl
import com.tarasovvp.smartblocker.utils.extensions.isAuthorisedUser
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SettingsBlockerUseCaseUnitTest {
    @MockK
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @MockK
    private lateinit var dataStoreRepository: DataStoreRepository

    @MockK
    private lateinit var firebaseAuth: FirebaseAuth

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    private lateinit var settingsBlockerUseCase: SettingsBlockerUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic("com.tarasovvp.smartblocker.utils.extensions.DeviceExtensionsKt")
        every { firebaseAuth.isAuthorisedUser() } returns true
        settingsBlockerUseCase =
            SettingsBlockerUseCaseImpl(realDataBaseRepository, dataStoreRepository, firebaseAuth)
    }

    @Test
    fun getBlockerTurnOffTest() =
        runBlocking {
            val blockerTurnOff = true
            coEvery { dataStoreRepository.blockerTurnOn() } returns flowOf(blockerTurnOff)
            val result = settingsBlockerUseCase.getBlockerTurnOn().single()
            assertEquals(blockerTurnOff, result)
            coVerify { dataStoreRepository.blockerTurnOn() }
        }

    @Test
    fun setBlockerTurnOffTest() =
        runBlocking {
            val blockerTurnOff = true
            coEvery { dataStoreRepository.setBlockerTurnOn(blockerTurnOff) } just Runs
            every { realDataBaseRepository.changeBlockTurnOn(any(), any()) } just Runs
            settingsBlockerUseCase.changeBlockTurnOn(blockerTurnOff, true, resultMock)
            coVerify { realDataBaseRepository.changeBlockTurnOn(any(), any()) }
        }

    @Test
    fun getBlockHiddenTest() =
        runBlocking {
            val blockHidden = true
            coEvery { dataStoreRepository.blockerTurnOn() } returns flowOf(blockHidden)
            val result = settingsBlockerUseCase.getBlockerTurnOn().single()
            assertEquals(blockHidden, result)
            coVerify { dataStoreRepository.blockerTurnOn() }
        }

    @Test
    fun setBlockHiddenTest() =
        runBlocking {
            val blockHidden = true
            every { realDataBaseRepository.changeBlockHidden(eq(blockHidden), any()) } just Runs
            settingsBlockerUseCase.changeBlockHidden(blockHidden, true, resultMock)
            verify { realDataBaseRepository.changeBlockHidden(eq(blockHidden), any()) }
        }

    @Test
    fun changeBlockHiddenTest() =
        runBlocking {
            every { firebaseAuth.currentUser } returns mockk()
            every { realDataBaseRepository.changeBlockHidden(eq(true), any()) } answers {
                resultMock.invoke(Result.Success())
            }
            settingsBlockerUseCase.changeBlockHidden(
                blockHidden = true,
                isNetworkAvailable = true,
                result = resultMock,
            )
            verify(exactly = 1) { resultMock.invoke(Result.Success()) }
        }

    @Test
    fun getCurrentCountryCodeTest() =
        runBlocking {
            val countryCode = CountryCode()
            coEvery { dataStoreRepository.getCountryCode() } returns flowOf(countryCode)
            val result = settingsBlockerUseCase.getCurrentCountryCode().single()
            assertEquals(countryCode, result)
            coVerify { dataStoreRepository.getCountryCode() }
        }

    @Test
    fun setCurrentCountryCodeTest() =
        runBlocking {
            val countryCode = CountryCode()
            coEvery { realDataBaseRepository.changeCountryCode(eq(countryCode), any()) } just Runs
            settingsBlockerUseCase.changeCountryCode(countryCode, true, resultMock)
            verify { realDataBaseRepository.changeCountryCode(eq(countryCode), any()) }
        }
}
