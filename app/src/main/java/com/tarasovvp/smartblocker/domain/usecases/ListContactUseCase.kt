package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter

interface ListContactUseCase {
    suspend fun allContactWithFilters(): List<ContactWithFilter>
}
