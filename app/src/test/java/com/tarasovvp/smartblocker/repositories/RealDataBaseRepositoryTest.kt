package com.tarasovvp.smartblocker.repositories

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_EMAIL
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_NUMBER
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_REVIEW
import com.tarasovvp.smartblocker.data.repositoryImpl.RealDataBaseRepositoryImpl
import com.tarasovvp.smartblocker.domain.models.Review
import com.tarasovvp.smartblocker.domain.models.entities.CurrentUser
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.models.entities.FilteredCall
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTERED_CALL_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.USERS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RealDataBaseRepositoryTest {

    @Mock
    private lateinit var databaseReference: DatabaseReference

    @Mock
    private lateinit var resultMock: () -> Unit

    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        realDataBaseRepository = RealDataBaseRepositoryImpl(databaseReference, TEST_NUMBER)
    }

    @Test
    fun getCurrentUserTest() {
        val resultMock = mock<(CurrentUser?) -> Unit>()
        val task = mock<Task<DataSnapshot>>()
        val snapshot = mock<DataSnapshot>()
        val child1 = mock<DataSnapshot>()
        val child2 = mock<DataSnapshot>()
        val filterChild = mock<DataSnapshot>()
        val filteredCallChild = mock<DataSnapshot>()
        val filter = Filter(TEST_FILTER)
        val filteredCall = FilteredCall()

        `when`(databaseReference.child(anyString())).thenReturn(databaseReference)
        `when`(databaseReference.get()).thenReturn(task)
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.result).thenReturn(snapshot)
        `when`(snapshot.children).thenReturn(listOf(child1, child2))
        `when`(child1.key).thenReturn(FILTER_LIST)
        `when`(child2.key).thenReturn(FILTERED_CALL_LIST)
        `when`(child1.children).thenReturn(listOf(filterChild))
        `when`(child2.children).thenReturn(listOf(filteredCallChild))
        `when`(filterChild.getValue(Filter::class.java)).thenReturn(filter)
        `when`(filteredCallChild.getValue(FilteredCall::class.java)).thenReturn(filteredCall)
        `when`(task.addOnCompleteListener(any())).thenAnswer {
            val listener = it.getArgument<OnCompleteListener<DataSnapshot>>(0)
            listener.onComplete(task)
            task
        }
        realDataBaseRepository.getCurrentUser(resultMock)

        verify(databaseReference, times(2)).child(USERS)
        verify(databaseReference, times(2)).child(TEST_NUMBER)
        verify(databaseReference).get()
        verify(resultMock).invoke(any())
    }

    @Test
    fun insertFilterTest() {
        val filter = Filter(TEST_FILTER)
        val task = mock<Task<Void>>()
        `when`(databaseReference.child(anyString())).thenReturn(databaseReference)
        `when`(databaseReference.setValue(any())).thenReturn(task)
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.addOnCompleteListener(any())).thenAnswer {
            val listener = it.getArgument<OnCompleteListener<Void>>(0)
            listener.onComplete(task)
            task
        }
        realDataBaseRepository.insertFilter(filter, resultMock)
        verify(task).addOnCompleteListener(any())
        verify(resultMock).invoke()
    }

    @Test
    fun deleteFilterListTest() {
        val filter1 = "filter1"
        val filter2 = "filter2"
        val filterList = listOf(Filter(filter1), Filter(filter2))

        val task = mock<Task<DataSnapshot>>()
        val snapshot1 = mock<DataSnapshot>()
        val snapshot2 = mock<DataSnapshot>()
        val child1 = mock<DatabaseReference>()
        val child2 = mock<DatabaseReference>()

        `when`(databaseReference.child(anyString())).thenReturn(databaseReference)
        `when`(databaseReference.get()).thenReturn(task)
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.addOnCompleteListener(any())).thenAnswer {
            val listener = it.getArgument<OnCompleteListener<DataSnapshot>>(0)
            listener.onComplete(task)
            task
        }
        `when`(task.result).thenReturn(snapshot1)
        `when`(snapshot1.children).thenReturn(listOf(snapshot1, snapshot2))
        `when`(snapshot1.key).thenReturn(filter1)
        `when`(snapshot2.key).thenReturn(filter2)
        `when`(snapshot1.ref).thenReturn(child1)
        `when`(snapshot2.ref).thenReturn(child2)

        realDataBaseRepository.deleteFilterList(filterList, resultMock)

        verify(databaseReference).get()
        verify(child1).removeValue()
        verify(child2).removeValue()
        verify(resultMock).invoke()
    }

    @Test
    fun insertFilteredCallTest() {
        val filteredCall = FilteredCall(callId = 1)
        val task = mock<Task<Void>>()
        `when`(databaseReference.child(anyString())).thenReturn(databaseReference)
        `when`(databaseReference.setValue(any())).thenReturn(task)
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.addOnCompleteListener(any())).thenAnswer {
            val listener = it.getArgument<OnCompleteListener<Void>>(0)
            listener.onComplete(task)
            task
        }
        realDataBaseRepository.insertFilteredCall(filteredCall, resultMock)
        verify(task).addOnCompleteListener(any())
        verify(resultMock).invoke()
    }

    @Test
    fun deleteFilteredCallListTest() {
        val callId1 = "1"
        val callId2 = "2"
        val filteredCallIdList = listOf(callId1, callId2)

        val task = mock<Task<DataSnapshot>>()
        val snapshot1 = mock<DataSnapshot>()
        val snapshot2 = mock<DataSnapshot>()
        val child1 = mock<DatabaseReference>()
        val child2 = mock<DatabaseReference>()

        `when`(databaseReference.child(anyString())).thenReturn(databaseReference)
        `when`(databaseReference.get()).thenReturn(task)
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.addOnCompleteListener(any())).thenAnswer {
            val listener = it.getArgument<OnCompleteListener<DataSnapshot>>(0)
            listener.onComplete(task)
            task
        }
        `when`(task.result).thenReturn(snapshot1)
        `when`(snapshot1.children).thenReturn(listOf(snapshot1, snapshot2))
        `when`(snapshot1.key).thenReturn(callId1)
        `when`(snapshot2.key).thenReturn(callId2)
        `when`(snapshot1.ref).thenReturn(child1)
        `when`(snapshot2.ref).thenReturn(child2)

        realDataBaseRepository.deleteFilteredCallList(filteredCallIdList, resultMock)

        verify(databaseReference).get()
        verify(child1).removeValue()
        verify(child2).removeValue()
        verify(resultMock).invoke()
    }

    @Test
    fun changeBlockHiddenTest() {
        val task = mock<Task<Void>>()
        `when`(databaseReference.child(anyString())).thenReturn(databaseReference)
        `when`(databaseReference.setValue(any())).thenReturn(task)
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.addOnCompleteListener(any())).thenAnswer {
            val listener = it.getArgument<OnCompleteListener<Void>>(0)
            listener.onComplete(task)
            task
        }
        realDataBaseRepository.changeBlockHidden(true, resultMock)
        verify(task).addOnCompleteListener(any())
        verify(resultMock).invoke()
    }

    @Test
    fun insertReviewTest() {
        val review = Review(TEST_EMAIL,TEST_REVIEW, 1000)
        val task = mock<Task<Void>>()
        `when`(databaseReference.child(anyString())).thenReturn(databaseReference)
        `when`(databaseReference.setValue(any())).thenReturn(task)
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.addOnCompleteListener(any())).thenAnswer {
            val listener = it.getArgument<OnCompleteListener<Void>>(0)
            listener.onComplete(task)
            task
        }
        realDataBaseRepository.insertReview(review, resultMock)
        verify(task).addOnCompleteListener(any())
        verify(resultMock).invoke()
    }
}