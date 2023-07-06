package com.tarasovvp.smartblocker.repositories

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_ERROR_MESSAGE
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_REVIEW
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_USER_ID
import com.tarasovvp.smartblocker.data.repositoryImpl.RealDataBaseRepositoryImpl
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.entities.models.Review
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCK_HIDDEN
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTERED_CALL_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.REVIEWS
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.USERS
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class RealDataBaseRepositoryUnitTest {

    @MockK
    private lateinit var firebaseDatabase: FirebaseDatabase

    @MockK
    private lateinit var firebaseAuth: FirebaseAuth

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        realDataBaseRepository = RealDataBaseRepositoryImpl(firebaseDatabase, firebaseAuth)
    }

    @Test
    fun getCurrentUserTest() {
        val resultCurrentUserMock = mockk<(Result<CurrentUser>?) -> Unit>(relaxed = true)
        val filterKey = "filter_list"
        val filteredCallKey = "filtered_call_list"
        val filter = Filter("test_filter")
        val filteredCall = FilteredCall(callId = 123)
        val task = mockk<Task<DataSnapshot>>(relaxed = true)
        val dataSnapshot = mockk<DataSnapshot>()
        val filterChild = mockk<DataSnapshot>()
        val filteredCallChild = mockk<DataSnapshot>()
        every { firebaseDatabase.reference.child(USERS).child(any()).key } returns filterKey
        every {
            firebaseDatabase.reference.child(USERS).child(any()).get()
        } returns task
        val currentUser = mockk<FirebaseUser>()
        every { task.isSuccessful } returns true
        every { firebaseAuth.currentUser } returns currentUser
        every { currentUser.uid } returns TEST_USER_ID
        every { task.result } returns dataSnapshot
        every { dataSnapshot.children } returns listOf(filterChild, filteredCallChild)
        every { filterChild.key } returns filterKey
        every { filteredCallChild.key } returns filteredCallKey
        every { filterChild.children } returns listOf(mockk(relaxed = true))
        every { filteredCallChild.children } returns listOf(mockk(relaxed = true))
        every { filterChild.getValue(Filter::class.java) } returns filter
        every { filteredCallChild.getValue(FilteredCall::class.java) } returns filteredCall
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<DataSnapshot>>()
            listener.onComplete(task)
            task
        }
        realDataBaseRepository.getCurrentUser(resultCurrentUserMock)
        verify { task.addOnCompleteListener(any()) }
        verify { resultCurrentUserMock.invoke(Result.Success(CurrentUser())) }
    }

    @Test
    fun deleteCurrentUser() {
        val task = mockk<Task<Void>>()
        every { firebaseDatabase.reference.child(any()).child(any()).removeValue() } returns task
        val currentUser = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns currentUser
        every { currentUser.uid } returns TEST_USER_ID
        every { task.isSuccessful } returns true
        every { task.addOnCompleteListener(any()) } answers {
            val listener = arg<OnCompleteListener<Void>>(0)
            listener.onComplete(task)
            task
        }
        val exception = mockk<Exception>()
        every { task.addOnFailureListener(any()) } answers {
            val listener = arg<OnFailureListener>(0)
            listener.onFailure(exception)
            task
        }
        every { task.isSuccessful } returns false
        every { task.exception } returns exception
        every { exception.localizedMessage } returns TEST_ERROR_MESSAGE
        realDataBaseRepository.deleteCurrentUser(resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun insertFilterTest() {
        val filter = Filter(TEST_FILTER)
        val task = mockk<Task<Void>>(relaxed = true)
        val currentUser = mockk<FirebaseUser>()
        every { task.isSuccessful } returns true
        every { firebaseAuth.currentUser } returns currentUser
        every { currentUser.uid } returns TEST_USER_ID
        every {
            firebaseDatabase.reference.child(USERS).child(any()).child(FILTER_LIST).child(filter.filter).setValue(filter)
        } returns task
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<Void>>()
            listener.onComplete(task)
            task
        }
        realDataBaseRepository.insertFilter(filter, resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun deleteFilterListTest() {
        val filter1 = Filter("filter1")
        val filter2 = Filter("filter2")
        val filterList = listOf(filter1, filter2)
        val task = mockk<Task<DataSnapshot>>(relaxed = true)
        val dataSnapshot = mockk<DataSnapshot>()
        val child1 = mockk<DataSnapshot>()
        val child2 = mockk<DataSnapshot>()
        every {
            firebaseDatabase.reference.child(USERS).child(any()).child(FILTER_LIST).get()
        } returns task
        val currentUser = mockk<FirebaseUser>()
        every { task.isSuccessful } returns true
        every { firebaseAuth.currentUser } returns currentUser
        every { currentUser.uid } returns TEST_USER_ID
        every { task.result } returns dataSnapshot
        every { dataSnapshot.children } returns listOf(child1, child2)
        every { child1.key } returns filter1.filter
        every { child2.key } returns filter2.filter
        every { child1.ref } returns mockk(relaxed = true)
        every { child2.ref } returns mockk(relaxed = true)
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<DataSnapshot>>()
            listener.onComplete(task)
            task
        }
        realDataBaseRepository.deleteFilterList(filterList, resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
        //TODO check
        //verify { child1.ref.removeValue() }
        //verify { child2.ref.removeValue() }
    }

    @Test
    fun insertFilteredCallTest() {
        val filteredCall = FilteredCall(callId = 1)
        val task = mockk<Task<Void>>(relaxed = true)
        every {
            firebaseDatabase.reference.child(USERS).child(any()).child(FILTERED_CALL_LIST).child(filteredCall.callId.toString()).setValue(filteredCall)
        } returns task
        val currentUser = mockk<FirebaseUser>()
        every { task.isSuccessful } returns true
        every { firebaseAuth.currentUser } returns currentUser
        every { currentUser.uid } returns TEST_USER_ID
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<Void>>()
            listener.onComplete(task)
            task
        }
        realDataBaseRepository.insertFilteredCall(filteredCall, resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun deleteFilteredCallListTest() {
        val callId1 = "1"
        val callId2 = "2"
        val filteredCallIdList = listOf(callId1, callId2)

        val task = mockk<Task<DataSnapshot>>(relaxed = true)
        val snapshot1 = mockk<DataSnapshot>(relaxed = true)
        val snapshot2 = mockk<DataSnapshot>(relaxed = true)
        val child1 = mockk<DatabaseReference>(relaxed = true)
        val child2 = mockk<DatabaseReference>(relaxed = true)

        every {
            firebaseDatabase.reference.child(USERS).child(any()).child(FILTERED_CALL_LIST).get()
        } returns task
        val currentUser = mockk<FirebaseUser>()
        every { task.isSuccessful } returns true
        every { firebaseAuth.currentUser } returns currentUser
        every { currentUser.uid } returns TEST_USER_ID
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<DataSnapshot>>()
            listener.onComplete(task)
            task
        }
        every { task.result } returns snapshot1
        every { snapshot1.children } returns listOf(snapshot1, snapshot2)
        every { snapshot1.key } returns callId1
        every { snapshot2.key } returns callId2
        every { snapshot1.ref } returns child1
        every { snapshot2.ref } returns child2

        val resultMock = mockk<(Result<Unit>) -> Unit>(relaxed = true)

        realDataBaseRepository.deleteFilteredCallList(filteredCallIdList, resultMock)

        //TODO check
        //verify { databaseReference.child(callId1) }
        //verify { databaseReference.child(callId2) }
        verify { child1.removeValue() }
        verify { child2.removeValue() }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun changeBlockHiddenTest() {
        val task = mockk<Task<Void>>(relaxed = true)
        every {
            firebaseDatabase.reference.child(USERS).child(any()).child(BLOCK_HIDDEN).setValue(any())
        } returns task
        val currentUser = mockk<FirebaseUser>()
        every { task.isSuccessful } returns true
        every { firebaseAuth.currentUser } returns currentUser
        every { currentUser.uid } returns TEST_USER_ID
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<Void>>()
            listener.onComplete(task)
            task
        }
        realDataBaseRepository.changeBlockHidden(true, resultMock)
        verify {
            firebaseDatabase.reference.child(USERS).child(any()).child(BLOCK_HIDDEN).setValue(true)
        }
        verify { task.addOnCompleteListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }

    @Test
    fun insertReviewTest() {
        val review = Review(TEST_EMAIL, TEST_REVIEW, 1000)
        val task = mockk<Task<Void>>(relaxed = true)
        every {
            firebaseDatabase.reference.child(REVIEWS).child(review.time.toString()).setValue(review)
        } returns task
        every { task.isSuccessful } returns true
        every { task.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<Void>>()
            listener.onComplete(task)
            task
        }
        realDataBaseRepository.insertReview(review, resultMock)
        verify { task.addOnCompleteListener(any()) }
        verify { resultMock.invoke(Result.Success()) }
    }
}