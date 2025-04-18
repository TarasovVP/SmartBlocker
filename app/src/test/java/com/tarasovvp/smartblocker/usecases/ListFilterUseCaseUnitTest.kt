package com.tarasovvp.smartblocker.usecases

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_FILTER
import com.tarasovvp.smartblocker.domain.entities.dbentities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.dbentities.Filter
import com.tarasovvp.smartblocker.domain.entities.dbviews.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.domain.usecases.ListFilterUseCase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.presentation.main.number.list.listfilter.ListFilterUseCaseImpl
import com.tarasovvp.smartblocker.utils.extensions.isAuthorisedUser
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ListFilterUseCaseUnitTest {
    @MockK
    private lateinit var filterRepository: FilterRepository

    @MockK
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    @MockK
    private lateinit var firebaseAuth: FirebaseAuth

    @MockK
    private lateinit var dataStoreRepository: DataStoreRepository

    @MockK(relaxed = true)
    private lateinit var resultMock: (Result<Unit>) -> Unit

    private lateinit var listFilterUseCase: ListFilterUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic("com.tarasovvp.smartblocker.utils.extensions.DeviceExtensionsKt")
        every { firebaseAuth.isAuthorisedUser() } returns true
        listFilterUseCase =
            ListFilterUseCaseImpl(
                filterRepository,
                realDataBaseRepository,
                firebaseAuth,
                dataStoreRepository,
            )
    }

    @Test
    fun allFilterWithFilteredNumbersByTypeTest() =
        runBlocking {
            val filterList =
                listOf(
                    FilterWithFilteredNumber(filter = Filter(filter = TEST_FILTER)),
                    FilterWithFilteredNumber(filter = Filter(filter = "mockFilter2")),
                )
            coEvery { filterRepository.allFilterWithFilteredNumbersByType(BLOCKER) } returns filterList
            val result = listFilterUseCase.allFilterWithFilteredNumbersByType(isBlockerList = true)
            assertEquals(TEST_FILTER, result?.get(0)?.filter?.filter)
        }

    @Test
    fun deleteFilterListTest() =
        runBlocking {
            val filterList = listOf(Filter())
            every { firebaseAuth.currentUser } returns mockk()
            val expectedResult = Result.Success<Unit>()
            coEvery { realDataBaseRepository.deleteFilterList(eq(filterList), any()) } coAnswers {
                val callback = secondArg<(Result<Unit>) -> Unit>()
                callback.invoke(expectedResult)
            }
            coEvery { filterRepository.deleteFilterList(eq(filterList)) } just Runs

            listFilterUseCase.deleteFilterList(filterList, true, resultMock)

            verify { realDataBaseRepository.deleteFilterList(eq(filterList), any()) }
            coVerify { filterRepository.deleteFilterList(eq(filterList)) }
            verify { resultMock.invoke(expectedResult) }
        }

    @Test
    fun getCurrentCountryCodeTest() =
        runBlocking {
            val countryCode = CountryCode()
            coEvery { dataStoreRepository.getCountryCode() } returns flowOf(countryCode)
            val result = listFilterUseCase.getCurrentCountryCode().single()
            assertEquals(countryCode, result)
            coVerify { dataStoreRepository.getCountryCode() }
        }
}
