package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.models.CurrentUser
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import kotlinx.coroutines.flow.Flow

interface SettingsSignUpUseCase {

    suspend fun getAllFilters(): List<Filter>

    suspend fun getAllFilteredCalls(): List<FilteredCall>

    suspend fun getBlockHidden(): Flow<Boolean?>

    fun createUserWithEmailAndPassword(email: String, password: String, result: (Result<String>) -> Unit)

    fun createCurrentUser(currentUser: CurrentUser, result: (Result<Unit>) -> Unit)
}