package com.tarasovvp.smartblocker.repositories

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.TestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.TestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.data.repositoryImpl.AuthRepositoryImpl
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner.Silent::class)
class AuthRepositoryTest {

    @Mock
    private lateinit var auth: FirebaseAuth

    @Mock
    private lateinit var googleSignInClient: GoogleSignInClient

    @Mock
    private lateinit var resultMock: () -> Unit

    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authRepository = AuthRepositoryImpl(auth)
    }

    @Test
    fun sendPasswordResetEmailTest() {
        val task = mock<Task<Void>>()
        `when`(auth.sendPasswordResetEmail(TEST_EMAIL)).thenReturn(task)
        `when`(task.addOnCompleteListener(any())).thenAnswer {
            val listener = it.getArgument<OnCompleteListener<Void>>(0)
            listener.onComplete(task)
            task
        }
        authRepository.sendPasswordResetEmail(TEST_EMAIL, resultMock)
        verify(task).addOnCompleteListener(any())
        verify(resultMock).invoke()
    }

    @Test
    fun signInWithEmailAndPasswordTest() {
        val task = mock<Task<AuthResult>>()
        `when`(auth.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD)).thenReturn(task)
        `when`(task.addOnCompleteListener(any())).thenAnswer {
            val listener = it.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(task)
            task
        }
        authRepository.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, resultMock)
        verify(task).addOnCompleteListener(any())
        verify(resultMock).invoke()
    }

    @Test
    fun signInWithGoogleTest() {
        val task = mock<Task<AuthResult>>()
        `when`(auth.signInWithCredential(any())).thenReturn(task)
        `when`(task.addOnCompleteListener(any())).thenAnswer {
            val listener = it.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(task)
            task
        }
        authRepository.signInWithGoogle(TEST_EMAIL, resultMock)
        verify(task).addOnCompleteListener(any())
        verify(resultMock).invoke()
    }

    @Test
    fun createUserWithEmailAndPasswordTest() {
        val task = mock<Task<AuthResult>>()
        `when`(auth.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD)).thenReturn(task)
        `when`(task.addOnCompleteListener(any())).thenAnswer {
            val listener = it.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(task)
            task
        }
        authRepository.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, resultMock)
        verify(task).addOnCompleteListener(any())
        verify(resultMock).invoke()
    }

    @Test
    fun changePasswordTest() {
        val task = mock<Task<AuthResult>>()
        `when`(auth.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD)).thenReturn(task)
        `when`(task.addOnCompleteListener(any())).thenAnswer {
            val listener = it.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(task)
            task
        }
        authRepository.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, resultMock)
        verify(task).addOnCompleteListener(any())
        verify(resultMock).invoke()
    }

    @Test
    fun deleteUserTest() {
        val currentUser = mock<FirebaseUser>()
        val deleteTask = mock<Task<Void>>()

        `when`(auth.currentUser).thenReturn(currentUser)
        `when`(currentUser.delete()).thenReturn(deleteTask)
        `when`(deleteTask.addOnCompleteListener(any())).thenAnswer {
            val listener = it.getArgument<OnCompleteListener<Void>>(0)
            listener.onComplete(deleteTask)
            deleteTask
        }
        authRepository.deleteUser(googleSignInClient, resultMock)
        verify(deleteTask).addOnCompleteListener(any())
        verify(resultMock).invoke()
    }

    @Test
    fun signOutTest() {
        val task = mock<Task<Void>>()
        `when`(googleSignInClient.signOut()).thenReturn(task)
        `when`(task.addOnCompleteListener(any())).thenAnswer {
            val listener = it.getArgument<OnCompleteListener<Void>>(0)
            listener.onComplete(task)
            task
        }
        authRepository.signOut(googleSignInClient, resultMock)
        verify(googleSignInClient).signOut()
        verify(resultMock).invoke()
    }
}