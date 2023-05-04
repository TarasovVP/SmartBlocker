package com.tarasovvp.smartblocker.presentation.ui_models

import android.os.Parcelable
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import kotlinx.parcelize.Parcelize

@Parcelize
open class CallWithFilterUIModel(
    var callUIModel: CallUIModel? = CallUIModel(),
    var filterWithCountryCode: FilterWithCountryCode? = FilterWithCountryCode()
) : Parcelable, NumberDataUIModel()
