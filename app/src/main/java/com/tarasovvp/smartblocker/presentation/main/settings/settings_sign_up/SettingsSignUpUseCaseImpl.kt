package com.tarasovvp.smartblocker.presentation.main.settings.settings_sign_up

import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.SettingsSignUpUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsSignUpUseCaseImpl @Inject constructor(
    private val filterRepository: FilterRepository,
    private val filteredCallRepository: FilteredCallRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val authRepository: AuthRepository,
    private val realDataBaseRepository: RealDataBaseRepository
    ): SettingsSignUpUseCase {

    override suspend fun getAllFilters(): List<Filter> {
        return filterRepository.allFilters()
    }

    override suspend fun getAllFilteredCalls(): List<FilteredCall> {
        return filteredCallRepository.allFilteredCalls()
    }
    override suspend fun getBlockHidden(): Flow<Boolean?> {
        return dataStoreRepository.blockHidden()
    }

    override fun createUserWithEmailAndPassword(email: String, password: String, result: (Result<String>) -> Unit) = authRepository.createUserWithEmailAndPassword(email, password) { authResult ->
        result.invoke(authResult)
    }

    override fun createCurrentUser(currentUser: CurrentUser, result: (Result<Unit>) -> Unit) = realDataBaseRepository.createCurrentUser(currentUser) { authResult ->
        result.invoke(authResult)
    }
}