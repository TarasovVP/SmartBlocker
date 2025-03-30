package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.domain.entities.dbviews.ContactWithFilter

interface ListContactUseCase {
    suspend fun allContactWithFilters(): List<ContactWithFilter>
}
