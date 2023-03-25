package com.tarasovvp.smartblocker.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_account.SettingsAccountUseCase
import com.tarasovvp.smartblocker.presentation.main.settings.settings_account.SettingsAccountViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SettingsAccountViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var settingsAccountUseCase: SettingsAccountUseCase

    @Mock
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var viewModel: SettingsAccountViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel =
            SettingsAccountViewModel(application, settingsAccountUseCase)
    }

    @Test
    fun signOutTest() {
        Mockito.doAnswer {
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(settingsAccountUseCase).signOut(eq(googleSignInClient), any())
        viewModel.signOut(googleSignInClient)
        assertEquals(true, viewModel.successLiveData.value)
    }

    @Test
    fun changePasswordTest() {
        val currentPassword = "currentPassword"
        val newPassword = "newPassword"
        Mockito.doAnswer {
            val result = it.arguments[2] as () -> Unit
            result.invoke()
        }.`when`(settingsAccountUseCase).changePassword(eq(currentPassword), eq(newPassword), any())
        viewModel.changePassword(currentPassword, newPassword)
        assertEquals(true, viewModel.successChangePasswordLiveData.value)
    }

    @Test
    fun deleteUserTest() {
        Mockito.doAnswer {
            val result = it.arguments[1] as () -> Unit
            result.invoke()
        }.`when`(settingsAccountUseCase).deleteUser(eq(googleSignInClient), any())
        viewModel.deleteUser(googleSignInClient)
        assertEquals(true, viewModel.successLiveData.value)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}