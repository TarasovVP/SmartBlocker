package com.tarasovvp.smartblocker.domain.mappers

import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.presentation.ui_models.*

interface ContactWithFilterMapper {
    fun mapToUIModel(contactWithFilter: ContactWithFilter): ContactWithFilterUIModel
    fun mapFromUIModel(contactWithFilterUIModel: ContactWithFilterUIModel): ContactWithFilter
}