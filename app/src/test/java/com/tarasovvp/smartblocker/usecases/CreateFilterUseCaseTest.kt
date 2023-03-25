package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecase.number.create.CreateFilterUseCaseImpl
import com.tarasovvp.smartblocker.domain.usecase.number.details.details_filter.DetailsFilterUseCaseImpl
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
class CreateFilterUseCaseTest {

    @Mock
    private lateinit var contactRepository: ContactRepository

    @Mock
    private lateinit var countryCodeRepository: CountryCodeRepository

    @Mock
    private lateinit var filterRepository: FilterRepository

    @Mock
    private lateinit var logCallRepository: LogCallRepository

    private lateinit var listContactUseCaseImpl: CreateFilterUseCaseImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        listContactUseCaseImpl = CreateFilterUseCaseImpl(contactRepository, countryCodeRepository, filterRepository, logCallRepository)
    }


    suspend fun getCountryCodeWithCountry(country: String) = countryCodeRepository.getCountryCodeWithCountry(country)

    suspend fun getCountryCodeWithCode(code: Int) = countryCodeRepository.getCountryCodeWithCode(code)

    suspend fun getNumberDataList(): ArrayList<NumberData> {
            val contacts =  contactRepository.getContactsWithFilters()
            val calls =  logCallRepository.allCallNumberWithFilter()
            val contactList = contacts
            val callList = calls
            val numberDataList = ArrayList<NumberData>().apply {
                addAll(contactList)
                addAll(callList)
                sortBy {
                    if (it is ContactWithFilter) it.contact?.number?.replace(PLUS_CHAR.toString(), String.EMPTY) else if (it is LogCallWithFilter) it.call?.number?.replace(PLUS_CHAR.toString(), String.EMPTY) else String.EMPTY
                }
            }
            return numberDataList
    }

    suspend fun checkFilterExist(filterWithCountryCode: FilterWithCountryCode) = filterRepository.getFilter(filterWithCountryCode)

    suspend fun filterNumberDataList(filterWithCountryCode: FilterWithCountryCode?, numberDataList: ArrayList<NumberData>, color: Int) = contactRepository.filteredNumberDataList(filterWithCountryCode?.filter, numberDataList, color)

    suspend fun createFilter(filter: Filter,  result: () -> Unit) = filterRepository.insertFilter(filter) {
        result.invoke()
    }

    suspend fun updateFilter(filter: Filter,  result: () -> Unit) = filterRepository.updateFilter(filter) {
        result.invoke()
    }

    suspend fun deleteFilter(filter: Filter,  result: () -> Unit) = filterRepository.deleteFilterList(listOf(filter)) {
        result.invoke()
    }

}