package com.tarasovvp.smartblocker.domain.mappers

import com.tarasovvp.smartblocker.domain.models.entities.Contact
import com.tarasovvp.smartblocker.presentation.ui_models.ContactUIModel

class ContactMapper {

    fun mapToUIModel(contact: Contact): ContactUIModel {
        return ContactUIModel(
            id = contact.id,
            name = contact.name.orEmpty(),
            photoUrl = contact.photoUrl.orEmpty(),
            number = contact.number,
            filter = contact.filter
        )
    }
}