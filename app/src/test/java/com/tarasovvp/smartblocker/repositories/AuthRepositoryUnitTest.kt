package com.tarasovvp.smartblocker.repositories

import android.text.TextUtils
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_PASSWORD
import com.tarasovvp.smartblocker.data.repositoryImpl.AuthRepositoryImpl
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class AuthRepositoryUnitTest {
    @MockK
    private lateinit var firebaseAuth: FirebaseAuth

    @MockK
    private lateinit var googleSignInClient: GoogleSignInClient

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        authRepository = AuthRepositoryImpl(firebaseAuth, googleSignInClient)
    }

    @Test
    fun sendPasswordResetEmailTest() {
        val task = mockk<Task<Void>>(relaxed = true)
        every { firebaseAuth.sendPasswordResetEmail(TEST_EMAIL) } returns task
        every { task.addOnSuccessListener(any()) } answers {
            val listener = firstArg<OnSuccessListener<in Void>>()
            listener.onSuccess(null)
            task
        }
        authRepository.sendPasswordResetEmail(TEST_EMAIL, resultMock)
        verify { task.addOnSuccessListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun signInWithEmailAndPasswordTest() {
        val task = mockk<Task<AuthResult>>(relaxed = true)
        every { firebaseAuth.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD) } returns task
        every { task.addOnSuccessListener(any()) } answers {
            val listener = firstArg<OnSuccessListener<in Void>>()
            listener.onSuccess(null)
            task
        }
        authRepository.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, resultMock)
        verify { task.addOnSuccessListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun signInWithGoogleTest() {
        val task = mockk<Task<AuthResult>>(relaxed = true)
        every { firebaseAuth.signInWithCredential(any()) } returns task
        every { task.addOnSuccessListener(any()) } answers {
            val listener = firstArg<OnSuccessListener<in Void>>()
            listener.onSuccess(null)
            task
        }
        authRepository.signInWithGoogle(TEST_EMAIL, resultMock)
        verify { task.addOnSuccessListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun signInAnonymously() {
        val task = mockk<Task<AuthResult>>(relaxed = true)
        every { firebaseAuth.signInAnonymously() } returns task
        every { task.addOnSuccessListener(any()) } answers {
            val listener = firstArg<OnSuccessListener<in Void>>()
            listener.onSuccess(null)
            task
        }
        authRepository.signInAnonymously(resultMock)
        verify { task.addOnSuccessListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun createUserWithEmailAndPasswordTest() {
        val task = mockk<Task<AuthResult>>(relaxed = true)
        val result: (Result<String>) -> Unit = mockk(relaxed = true)
        every {
            firebaseAuth.createUserWithEmailAndPassword(
                TEST_EMAIL,
                TEST_PASSWORD,
            )
        } returns task
        every { task.isSuccessful } returns true
        every { task.result } returns mockk()
        every { task.result.user } returns mockk()
        every { task.result.user?.uid } returns TEST_EMAIL
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<AuthResult>>()
            listener.onComplete(task)
            task
        }
        authRepository.createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD, result)
        verify { task.addOnCompleteListener(any()) }
        verify { result.invoke(Result.Success(TEST_EMAIL)) }
    }

    @Test
    fun changePasswordTest() {
        mockkStatic(TextUtils::class)
        every { TextUtils.isEmpty(any()) } returns false
        val task = mockk<Task<AuthResult>>(relaxed = true)
        val task2 = mockk<Task<Void>>(relaxed = true)
        val currentUser = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns currentUser
        every { currentUser.email } returns TEST_EMAIL
        every { currentUser.reauthenticateAndRetrieveData(any()) } returns task
        every { currentUser.updatePassword(TEST_PASSWORD) } returns task2
        every {
            firebaseAuth.createUserWithEmailAndPassword(
                TEST_EMAIL,
                TEST_PASSWORD,
            )
        } returns task
        every { task.addOnSuccessListener(any()) } answers {
            val listener = firstArg<OnSuccessListener<in Void>>()
            listener.onSuccess(null)
            task
        }
        val resultCallback: (Result<Unit>) -> Unit = mockk(relaxed = true)
        authRepository.changePassword(TEST_EMAIL, TEST_PASSWORD, resultCallback)
        verify { task.addOnSuccessListener(any()) }
    }

    @Test
    fun deleteUserTest() {
        val currentUser = mockk<FirebaseUser>(relaxed = true)
        val deleteTask = mockk<Task<Void>>(relaxed = true)
        every { firebaseAuth.currentUser } returns currentUser
        every { authRepository.signOut(resultMock) } just Runs
        every { currentUser.delete() } returns deleteTask
        every { deleteTask.addOnSuccessListener(any()) } answers {
            val listener = firstArg<OnSuccessListener<in Void>>()
            listener.onSuccess(null)
            deleteTask
        }
        val task = mockk<Task<Void>>(relaxed = true)
        every { googleSignInClient.signOut() } returns task
        every { firebaseAuth.signOut() } returns Unit
        every { task.addOnSuccessListener(any()) } answers {
            val listener = firstArg<OnSuccessListener<in Void>>()
            listener.onSuccess(null)
            task
        }
        authRepository.deleteUser(resultMock)
        verify { deleteTask.addOnSuccessListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun signOutTest() {
        val task = mockk<Task<Void>>(relaxed = true)
        every { googleSignInClient.signOut() } returns task
        every { firebaseAuth.signOut() } returns Unit
        every { task.addOnSuccessListener(any()) } answers {
            val listener = firstArg<OnSuccessListener<in Void>>()
            listener.onSuccess(null)
            task
        }
        authRepository.signOut(resultMock)
        verify { task.addOnSuccessListener(any()) }
        verify { firebaseAuth.signOut() }
        verify { resultMock.invoke(Result.Success()) }
    }
}
