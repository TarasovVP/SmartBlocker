package com.tarasovvp.smartblocker.presentation.main.number.list.listcall

import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.domain.usecases.ListCallUseCase
import com.tarasovvp.smartblocker.utils.extensions.isAuthorisedUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ListCallUseCaseImpl
    @Inject
    constructor(
        private val logCallRepository: LogCallRepository,
        private val filteredCallRepository: FilteredCallRepository,
        private val realDataBaseRepository: RealDataBaseRepository,
        private val firebaseAuth: FirebaseAuth,
        private val dataStoreRepository: DataStoreRepository,
    ) : ListCallUseCase {
        override suspend fun allCallWithFilters() = logCallRepository.allCallWithFilters()

        override suspend fun deleteCallList(
            filteredCallIdList: List<Int>,
            isNetworkAvailable: Boolean,
            result: (Result<Unit>) -> Unit,
        ) {
            if (firebaseAuth.isAuthorisedUser()) {
                if (isNetworkAvailable) {
                    realDataBaseRepository.deleteFilteredCallList(filteredCallIdList.map(Int::toString)) {
                        runBlocking {
                            filteredCallRepository.deleteFilteredCalls(filteredCallIdList)
                            result.invoke(Result.Success())
                        }
                    }
                } else {
                    result.invoke(Result.Failure())
                }
            } else {
                filteredCallRepository.deleteFilteredCalls(filteredCallIdList)
                result.invoke(Result.Success())
            }
        }

        override suspend fun getReviewVoted(): Flow<Boolean?> {
            return dataStoreRepository.reviewVoted()
        }

        override fun setReviewVoted(result: (Result<Unit>) -> Unit) =
            realDataBaseRepository.setReviewVoted { operationResult ->
                runBlocking {
                    dataStoreRepository.setReviewVoted(true)
                }
                result.invoke(operationResult)
            }
    }
