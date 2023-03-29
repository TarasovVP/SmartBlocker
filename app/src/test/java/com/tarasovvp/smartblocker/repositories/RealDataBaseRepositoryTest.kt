package com.tarasovvp.smartblocker.repositories

import com.google.firebase.database.DatabaseReference
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.data.repositoryImpl.RealDataBaseRepositoryImpl
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.REVIEWS
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.USERS
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RealDataBaseRepositoryTest {

    @Mock
    private lateinit var database: DatabaseReference

    lateinit var realDataBaseRepository: RealDataBaseRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        realDataBaseRepository = RealDataBaseRepositoryImpl(database)
    }

    private var currentUserDatabase =
        database.child(USERS).child(SmartBlockerApp.instance?.auth?.currentUser?.uid.orEmpty())
    private var reviewsDatabase = database.child(REVIEWS)

    @Test
    fun getCurrentUserTest() {

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