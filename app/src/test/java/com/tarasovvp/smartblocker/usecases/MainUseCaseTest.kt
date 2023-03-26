package com.tarasovvp.smartblocker.usecases

import android.app.Application
import com.tarasovvp.smartblocker.domain.models.entities.*
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecase.main.MainUseCaseImpl
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

//TODO unfinished
@Suppress
@RunWith(MockitoJUnitRunner::class)
class MainUseCaseTest {

    @Mock
    private lateinit var contactRepository: ContactRepository

    @Mock
    private lateinit var countryCodeRepository: CountryCodeRepository

    @Mock
    private lateinit var filterRepository: FilterRepository

    @Mock
    private lateinit var logCallRepository: LogCallRepository

    @Mock
    private lateinit var filteredCallRepository: FilteredCallRepository

    @Mock
    private lateinit var realDataBaseRepository: RealDataBaseRepository

    private lateinit var settingsAccountUseCaseImpl: MainUseCaseImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        settingsAccountUseCaseImpl = MainUseCaseImpl(contactRepository, countryCodeRepository, filterRepository, logCallRepository, filteredCallRepository, realDataBaseRepository)
    }

    fun getCurrentUser(result: (CurrentUser?) -> Unit) = realDataBaseRepository.getCurrentUser {
        result.invoke(it)
    }

    suspend fun getAllFilters(): List<Filter> {
        return filterRepository.allFilters()
    }

    suspend fun getSystemCountryCodeList(result: (Int, Int) -> Unit) = countryCodeRepository.getSystemCountryCodeList { size, position ->
        result.invoke(size, position)
    }

    suspend fun insertAllCountryCodes(countryCodeList: List<CountryCode>) {
        countryCodeRepository.insertAllCountryCodes(countryCodeList)
    }

    suspend fun getSystemContactList(application: Application, result: (Int, Int) -> Unit) = contactRepository.getSystemContactList(application) { size, position ->
        result.invoke(size, position)
    }

    suspend fun setFilterToContact(
        filterList: List<Filter>,
        contactList: List<Contact>,
        result: (Int, Int) -> Unit,
    ) = contactRepository.setFilterToContact(filterList, contactList) { size, position ->
        result.invoke(size, position)
    }

    suspend fun insertContacts(contactList: List<Contact>) {
        contactRepository.insertContacts(contactList)
    }

    suspend fun getSystemLogCallList(application: Application, result: (Int, Int) -> Unit) = logCallRepository.getSystemLogCallList(application) { size, position ->
        result.invoke(size, position)
    }

    suspend fun setFilterToLogCall(
        filterList: List<Filter>,
        logCallList: List<LogCall>,
        result: (Int, Int) -> Unit,
    ) = logCallRepository.setFilterToLogCall(filterList, logCallList) { size, position ->
        result.invoke(size, position)
    }

    suspend fun insertAllLogCalls(logCallList: List<LogCall>) {
        logCallRepository.insertAllLogCalls(logCallList)
    }

    suspend fun getAllFilteredCalls(): List<FilteredCall> {
        return filteredCallRepository.allFilteredCalls()
    }

    suspend fun setFilterToFilteredCall(
        filterList: List<Filter>,
        filteredCallList: List<FilteredCall>,
        result: (Int, Int) -> Unit,
    ) = filteredCallRepository.setFilterToFilteredCall(filterList, filteredCallList) { size, position ->
        result.invoke(size, position)
    }

    suspend fun insertAllFilteredCalls(filteredCallList: List<FilteredCall>) {
        filteredCallRepository.insertAllFilteredCalls(filteredCallList)
    }

    suspend fun insertAllFilters(filterList: List<Filter>) {
        filterRepository.insertAllFilters(filterList)
    }
}