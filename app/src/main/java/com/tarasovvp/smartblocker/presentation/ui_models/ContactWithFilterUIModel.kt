package com.tarasovvp.smartblocker.presentation.ui_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactWithFilterUIModel(
    var contactUIModel: ContactUIModel? = ContactUIModel(),
    var filterWithCountryCodeUIModel: FilterWithCountryCodeUIModel? = FilterWithCountryCodeUIModel()
) : Parcelable, NumberDataUIModel()
