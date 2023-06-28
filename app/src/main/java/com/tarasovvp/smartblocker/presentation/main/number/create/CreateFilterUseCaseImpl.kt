package com.tarasovvp.smartblocker.presentation.main.number.create

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.CreateFilterUseCase
import com.tarasovvp.smartblocker.utils.AppPhoneNumberUtil
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class CreateFilterUseCaseImpl @Inject constructor(
    private val contactRepository: ContactRepository,
    private val phoneNumberUtil: AppPhoneNumberUtil,
    private val filterRepository: FilterRepository,
    private val realDataBaseRepository: RealDataBaseRepository,
    private val firebaseAuth: FirebaseAuth
) : CreateFilterUseCase {

    override suspend fun allContactsWithFiltersByCreateFilter(filter: String, country: String, countryCode: String, isContain: Boolean): List<ContactWithFilter> {
        val contactWithFilters = contactRepository.allContactsWithFiltersByCreateFilter(filter)
        val  filteredContactWithFilters = when {
            isContain -> contactWithFilters
            else -> contactWithFilters.filter { contactWithFilter ->
                    phoneNumberUtil.phoneNumberValue(contactWithFilter.contact?.number, phoneNumberUtil.getPhoneNumber(contactWithFilter.contact?.number, country)).startsWith("$countryCode$filter")
            }
        }
        return filteredContactWithFilters
    }

    override suspend fun getFilter(filter: String) = filterRepository.getFilter(filter)

    override suspend fun createFilter(filter: Filter,  isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit) {
        if (firebaseAuth.currentUser.isNotNull()) {
            if (isNetworkAvailable) {
                realDataBaseRepository.insertFilter(filter) {
                    runBlocking {
                        filterRepository.insertFilter(filter)
                        result.invoke(Result.Success())
                    }
                }
            } else {
                result.invoke(Result.Failure())
            }
        } else {
            filterRepository.insertFilter(filter)
            result.invoke(Result.Success())
        }
    }

    override suspend fun updateFilter(filter: Filter,  isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit) {
        if (firebaseAuth.currentUser.isNotNull()) {
            if (isNetworkAvailable) {
                realDataBaseRepository.insertFilter(filter) {
                    runBlocking {
                        filterRepository.updateFilter(filter)
                        result.invoke(Result.Success())
                    }
                }
            } else {
                result.invoke(Result.Failure())
            }
        } else {
            filterRepository.updateFilter(filter)
            result.invoke(Result.Success())
        }
    }

    override suspend fun deleteFilter(filter: Filter,  isNetworkAvailable: Boolean, result: (Result<Unit>) -> Unit) {
        if (firebaseAuth.currentUser.isNotNull()) {
            if (isNetworkAvailable) {
                realDataBaseRepository.deleteFilterList(listOf(filter)) {
                    runBlocking {
                        filterRepository.deleteFilterList(listOf(filter))
                        result.invoke(Result.Success())
                    }
                }
            } else {
                result.invoke(Result.Failure())
            }
        } else {
            filterRepository.deleteFilterList(listOf(filter))
            result.invoke(Result.Success())
        }
    }
}