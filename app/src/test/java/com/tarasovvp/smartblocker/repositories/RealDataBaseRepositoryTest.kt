package com.tarasovvp.smartblocker.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.tarasovvp.smartblocker.data.repositoryImpl.RealDataBaseRepositoryImpl
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
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
class RealDataBaseRepositoryTest {

    @Mock
    private lateinit var database: DatabaseReference

    private lateinit var realDataBaseRepository: RealDataBaseRepository

    private val resultMock = mock<() -> Unit>()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        realDataBaseRepository = RealDataBaseRepositoryImpl(database)
    }

    @Test
    fun getCurrentUserTest() {
        val filter = Filter("test")
        val task = mock<Task<Void>>()
        Mockito.`when`(database.child(filter.filter)).thenReturn(database)
        Mockito.`when`(database.setValue(filter)).thenReturn(task)
        Mockito.doAnswer {
            val result = it.arguments[0] as () -> Unit
            result.invoke()
        }.`when`(task).addOnCompleteListener(any())

        realDataBaseRepository.insertFilter(filter, resultMock)
        verify(task).addOnCompleteListener(any())
        verify(resultMock).invoke()
    }

    @Test
    fun insertFilterTest() {

    }

    @Test
    fun deleteFilterListTest() {

    }

    @Test
    fun insertFilteredCallTest() {

    }

    @Test
    fun deleteFilteredCallListTest() {

    }

    @Test
    fun changeBlockHiddenTest() {

    }

    @Test
    fun insertReviewTest() {

    }
}