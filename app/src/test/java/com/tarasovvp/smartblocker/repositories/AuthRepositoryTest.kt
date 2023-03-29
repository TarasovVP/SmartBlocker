package com.tarasovvp.smartblocker.repositories

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.TestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.data.repositoryImpl.AuthRepositoryImpl
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryTest {

    @Mock
    private lateinit var auth: FirebaseAuth

    @Mock
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var authRepository: AuthRepository

    private val resultMock = mock<() -> Unit>()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authRepository = AuthRepositoryImpl(auth)
    }

    @Test
    fun sendPasswordResetEmailTest() {
        val task = mock<Task<Void>>()
        Mockito.`when`(auth.sendPasswordResetEmail(TEST_EMAIL)).thenReturn(task)
        Mockito.doAnswer {
            @Suppress("UNCHECKED_CAST")
            val result = it.arguments[0] as () -> Unit
            result.invoke()
        }.`when`(task).addOnCompleteListener(any())

        authRepository.sendPasswordResetEmail(TEST_EMAIL, resultMock)
        verify(resultMock).invoke()
    }

    @Test
    fun signInWithEmailAndPasswordTest() {

    }

    @Test
    fun signInWithGoogleTest() {

    }

    @Test
    fun createUserWithEmailAndPasswordTest() {

    }

    @Test
    fun changePasswordTest() {

    }

    @Test
    fun deleteUserTest() {

    }

    @Test
    fun signOutTest() {

    }
}