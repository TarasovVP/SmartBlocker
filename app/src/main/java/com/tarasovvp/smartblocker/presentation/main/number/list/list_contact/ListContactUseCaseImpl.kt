package com.tarasovvp.smartblocker.presentation.main.number.list.list_contact

import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.domain.usecases.ListContactUseCase
import javax.inject.Inject

class ListContactUseCaseImpl
    @Inject
    constructor(private val contactRepository: ContactRepository) :
    ListContactUseCase {
        override suspend fun allContactWithFilters() = contactRepository.allContactWithFilters()
    }
