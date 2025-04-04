package com.tarasovvp.smartblocker.presentation.main.number.list.listfilter

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.entities.dbentities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.dbentities.Filter
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.domain.usecases.ListFilterUseCase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.utils.extensions.isAuthorisedUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ListFilterUseCaseImpl
    @Inject
    constructor(
        private val filterRepository: FilterRepository,
        private val realDataBaseRepository: RealDataBaseRepository,
        private val firebaseAuth: FirebaseAuth,
        private val dataStoreRepository: DataStoreRepository,
    ) : ListFilterUseCase {
        override suspend fun allFilterWithFilteredNumbersByType(isBlockerList: Boolean) =
            filterRepository.allFilterWithFilteredNumbersByType(if (isBlockerList) BLOCKER else PERMISSION)

        override suspend fun deleteFilterList(
            filterList: List<Filter>,
            isNetworkAvailable: Boolean,
            result: (Result<Unit>) -> Unit,
        ) {
            if (firebaseAuth.isAuthorisedUser()) {
                if (isNetworkAvailable) {
                    realDataBaseRepository.deleteFilterList(filterList) {
                        runBlocking {
                            filterRepository.deleteFilterList(filterList)
                            result.invoke(Result.Success())
                        }
                    }
                } else {
                    filterRepository.deleteFilterList(filterList)
                    result.invoke(Result.Failure())
                }
            } else {
                filterRepository.deleteFilterList(filterList)
                result.invoke(Result.Success())
            }
        }

        override suspend fun getCurrentCountryCode(): Flow<CountryCode?> {
            return dataStoreRepository.getCountryCode()
        }
    }
