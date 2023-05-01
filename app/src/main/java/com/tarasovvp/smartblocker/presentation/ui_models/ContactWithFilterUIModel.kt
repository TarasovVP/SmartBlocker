package com.tarasovvp.smartblocker.presentation.ui_models

import android.os.Parcelable
import com.tarasovvp.smartblocker.domain.models.NumberData
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactWithFilterUIModel(
    var contactUIModel: ContactUIModel? = ContactUIModel(),
    var filterWithCountryCodeUIModel: FilterWithCountryCodeUIModel? = FilterWithCountryCodeUIModel()
) : Parcelable, NumberData()
