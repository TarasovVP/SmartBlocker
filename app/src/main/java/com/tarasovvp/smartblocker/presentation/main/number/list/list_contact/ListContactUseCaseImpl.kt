package com.tarasovvp.smartblocker.presentation.main.number.list.list_contact

import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.domain.usecases.ListContactUseCase
import com.tarasovvp.smartblocker.utils.extensions.digitsTrimmed
import com.tarasovvp.smartblocker.utils.extensions.isContaining
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListContactUseCaseImpl @Inject constructor(private val contactRepository: ContactRepository):
    ListContactUseCase {

    override suspend fun allContactWithFilters() = contactRepository.allContactWithFilters()

    override suspend fun getFilteredContactList(
        contactList: List<ContactWithFilter>,
        searchQuery: String,
        filterIndexes: ArrayList<Int>,
    ) = withContext(Dispatchers.Default) {
        if (searchQuery.isBlank() && filterIndexes.isEmpty()) contactList else contactList.filter { contactWithFilter ->
            ((contactWithFilter.contact?.name isContaining searchQuery || contactWithFilter.contact?.number.digitsTrimmed() isContaining searchQuery))
                    && (contactWithFilter.filterWithCountryCode?.filter?.isBlocker().isTrue() && filterIndexes.contains(
                NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal)
                    || contactWithFilter.filterWithCountryCode?.filter?.isPermission().isTrue() && filterIndexes.contains(
                NumberDataFiltering.CONTACT_WITH_PERMISSION.ordinal)
                    || filterIndexes.isEmpty())
        }
    }
}