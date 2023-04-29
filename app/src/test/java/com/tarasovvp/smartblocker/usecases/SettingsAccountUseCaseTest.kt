package com.tarasovvp.smartblocker.usecases

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_account.SettingsAccountUseCase
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_account.SettingsAccountUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

class SettingsAccountUseCaseTest {

    @MockK
    private lateinit var authRepository: AuthRepository

    @MockK
    private lateinit var googleSignInClient: GoogleSignInClient

    @MockK(relaxed = true)
    private lateinit var resultMock: () -> Unit

    private lateinit var settingsAccountUseCase: SettingsAccountUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        settingsAccountUseCase = SettingsAccountUseCaseImpl(authRepository)
    }

    @Test
    fun signOutTest() {
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(authRepository).signOut(eq(googleSignInClient), any())
        settingsAccountUseCase.signOut(googleSignInClient, resultMock)
        verify(resultMock).invoke()
    }

    @Test
    fun changePasswordTest() {
        val newPassword = "newPassword"
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[2] as () -> Unit
            result.invoke()
        }.`when`(authRepository).changePassword(eq(TEST_PASSWORD), eq(newPassword), any())
        settingsAccountUseCase.changePassword(TEST_PASSWORD, newPassword, resultMock)
        verify(resultMock).invoke()
    }

    @Test
    fun deleteUserTest() {
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(authRepository).deleteUser(eq(googleSignInClient), any())
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(authRepository).signOut(eq(googleSignInClient), any())
        settingsAccountUseCase.deleteUser(googleSignInClient, resultMock)
        verify(resultMock).invoke()
    }
}