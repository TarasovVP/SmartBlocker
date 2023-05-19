package com.tarasovvp.smartblocker.usecases

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecases.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker.SettingsBlockerUseCaseImpl
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SettingsBlockerUseCaseTest {

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
        settingsBlockerUseCase = SettingsBlockerUseCaseImpl(realDataBaseRepository, dataStoreRepository, firebaseAuth)
    }

    @Test
    fun changeBlockHiddenTest() {
        every { realDataBaseRepository.changeBlockHidden(eq(true), any()) } answers {
            resultMock.invoke(Result.Success())
        }
        settingsBlockerUseCase.changeBlockHidden(blockHidden = true, isNetworkAvailable = true, result = resultMock
        )
        verify(exactly = 1) { resultMock.invoke(Result.Success()) }
    }
}