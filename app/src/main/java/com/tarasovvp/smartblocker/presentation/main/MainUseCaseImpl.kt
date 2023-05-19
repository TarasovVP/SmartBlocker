package com.tarasovvp.smartblocker.presentation.main

import android.app.Application
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.MainUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Locale
import javax.inject.Inject

class MainUseCaseImpl @Inject constructor(
    private val contactRepository: ContactRepository,
    private val countryCodeRepository: CountryCodeRepository,
    private val filterRepository: FilterRepository,
    private val logCallRepository: LogCallRepository,
    private val filteredCallRepository: FilteredCallRepository,
    private val realDataBaseRepository: RealDataBaseRepository,
    private val dataStoreRepository: DataStoreRepository
): MainUseCase {

    override suspend fun getAppLanguage(): Flow<String?> {
        return dataStoreRepository.getAppLang()
    }

    override suspend fun setAppLanguage(appLang: String) {
        return dataStoreRepository.setAppLang(appLang)
    }

    override suspend fun getAppTheme(): Flow<Int?> {
        return dataStoreRepository.getAppTheme()
    }

    override suspend fun getOnBoardingSeen(): Flow<Boolean?> {
        return dataStoreRepository.onBoardingSeen()
    }

    override suspend fun getBlockerTurnOff(): Flow<Boolean?> {
        return dataStoreRepository.blockerTurnOff()
    }

    override suspend fun setBlockHidden(blockHidden: Boolean) {
        dataStoreRepository.setBlockHidden(blockHidden)
    }

    override fun getCurrentUser(result: (Result<CurrentUser>) -> Unit) = realDataBaseRepository.getCurrentUser { operationResult ->
        result.invoke(operationResult)
    }

    override suspend fun getSystemCountryCodes(result: (Int, Int) -> Unit) = countryCodeRepository.getSystemCountryCodeList { size, position ->
        result.invoke(size, position)
    }

    override suspend fun getCurrentCountryCode(): Flow<CountryCode?> {
        return dataStoreRepository.getCountryCode()
    }

    override suspend fun setCurrentCountryCode(countryCode: CountryCode) {
        dataStoreRepository.setCountryCode(countryCode)
    }

    override suspend fun insertAllCountryCodes(countryCodeList: List<CountryCode>) {
        countryCodeRepository.insertAllCountryCodes(countryCodeList)
    }

    override suspend fun getAllFilters(): List<Filter> {
        return filterRepository.allFilters()
    }

    override suspend fun getSystemContacts(application: Application, result: (Int, Int) -> Unit): ArrayList<Contact> {
        val country = dataStoreRepository.getCountryCode().first()?.country ?: Locale.getDefault().country
        return contactRepository.getSystemContactList(application, country) { size, position ->
            result.invoke(size, position)
        }
    }

    override suspend fun insertAllContacts(contactList: List<Contact>) {
        contactRepository.insertAllContacts(contactList)
    }

    override suspend fun getSystemLogCalls(application: Application, result: (Int, Int) -> Unit): List<LogCall> {
        val country = dataStoreRepository.getCountryCode().first()?.country ?: Locale.getDefault().country
        return logCallRepository.getSystemLogCallList(application, country) { size, position ->
            result.invoke(size, position)
        }
    }

    override suspend fun insertAllLogCalls(logCallList: List<LogCall>) {
        logCallRepository.insertAllLogCalls(logCallList)
    }

    override suspend fun insertAllFilteredCalls(filteredCallList: List<FilteredCall>) {
        filteredCallRepository.insertAllFilteredCalls(filteredCallList)
    }

    override suspend fun insertAllFilters(filterList: List<Filter>) {
        filterRepository.insertAllFilters(filterList)
    }
}