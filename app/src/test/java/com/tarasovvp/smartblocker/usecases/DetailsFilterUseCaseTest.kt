package com.tarasovvp.smartblocker.usecases

import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecase.number.details.details_filter.DetailsFilterUseCaseImpl
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

//TODO unfinished
@Suppress
@RunWith(MockitoJUnitRunner::class)
class DetailsFilterUseCaseTest {

    @Mock
    private lateinit var contactRepository: ContactRepository

    @Mock
    private lateinit var filterRepository: FilterRepository

    @Mock
    private lateinit var logCallRepository: LogCallRepository

    @Mock
    private lateinit var filteredCallRepository: FilteredCallRepository

    private lateinit var listContactUseCaseImpl: DetailsFilterUseCaseImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        listContactUseCaseImpl = DetailsFilterUseCaseImpl(contactRepository, filterRepository, logCallRepository, filteredCallRepository)
    }

    suspend fun getQueryContactCallList(filter: Filter): ArrayList<NumberData> {
            val calls =  logCallRepository.getLogCallWithFilterByFilter(filter.filter)
            val contacts =  contactRepository.getContactsWithFilterByFilter(filter.filter)
            val callList = calls
            val contactList = contacts
            val numberDataList = ArrayList<NumberData>().apply {
                addAll(callList)
                addAll(contactList)
                sortBy {
                    if (it is ContactWithFilter) it.contact?.number else if (it is LogCallWithFilter) it.call?.number else String.EMPTY
                }
            }
            return numberDataList
        }

    suspend fun filteredNumberDataList(filter: Filter?, numberDataList: ArrayList<NumberData>, color: Int, ) = contactRepository.filteredNumberDataList(filter, numberDataList, color)

    suspend fun filteredCallsByFilter(filter: String) = filteredCallRepository.filteredCallsByFilter(filter)

    suspend fun deleteFilter(filter: Filter, result: () -> Unit) = filterRepository.deleteFilterList(listOf(filter)) {
        result.invoke()
    }

    suspend fun updateFilter(filter: Filter, result: () -> Unit) = filterRepository.updateFilter(filter) {
        result.invoke()
    }
}