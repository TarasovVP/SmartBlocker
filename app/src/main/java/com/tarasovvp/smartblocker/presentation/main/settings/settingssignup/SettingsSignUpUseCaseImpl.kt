package com.tarasovvp.smartblocker.presentation.main.settings.settingssignup

import com.tarasovvp.smartblocker.domain.entities.dbentities.Filter
import com.tarasovvp.smartblocker.domain.entities.dbentities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.sealedclasses.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsSignUpUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsSignUpUseCaseImpl
    @Inject
    constructor(
        private val filterRepository: FilterRepository,
        private val filteredCallRepository: FilteredCallRepository,
        private val dataStoreRepository: DataStoreRepository,
        private val authRepository: AuthRepository,
        private val realDataBaseRepository: RealDataBaseRepository,
    ) : SettingsSignUpUseCase {
        override suspend fun getAllFilters(): List<Filter> {
            return filterRepository.allFilters()
        }

        override suspend fun getAllFilteredCalls(): List<FilteredCall> {
            return filteredCallRepository.allFilteredCalls()
        }

        override suspend fun getBlockHidden(): Flow<Boolean?> {
            return dataStoreRepository.blockHidden()
        }

        override fun fetchSignInMethodsForEmail(
            email: String,
            result: (Result<List<String>>) -> Unit,
        ) = authRepository.fetchSignInMethodsForEmail(email) { authResult ->
            result.invoke(authResult)
        }

        override fun createUserWithGoogle(
            idToken: String,
            result: (Result<Unit>) -> Unit,
        ) = authRepository.signInWithGoogle(idToken) { authResult ->
            result.invoke(authResult)
        }

        override fun createUserWithEmailAndPassword(
            email: String,
            password: String,
            result: (Result<String>) -> Unit,
        ) = authRepository.createUserWithEmailAndPassword(email, password) { authResult ->
            result.invoke(authResult)
        }

        override fun signInWithEmailAndPassword(
            email: String,
            password: String,
            result: (Result<Unit>) -> Unit,
        ) = authRepository.signInWithEmailAndPassword(email, password) { authResult ->
            result.invoke(authResult)
        }

        override fun createCurrentUser(
            currentUser: CurrentUser,
            result: (Result<Unit>) -> Unit,
        ) = realDataBaseRepository.createCurrentUser(currentUser) { authResult ->
            result.invoke(authResult)
        }

        override fun updateCurrentUser(
            currentUser: CurrentUser,
            result: (Result<Unit>) -> Unit,
        ) = realDataBaseRepository.updateCurrentUser(currentUser) { authResult ->
            result.invoke(authResult)
        }
    }
