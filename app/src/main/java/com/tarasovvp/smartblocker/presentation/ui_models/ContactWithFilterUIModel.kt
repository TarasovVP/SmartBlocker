package com.tarasovvp.smartblocker.presentation.ui_models

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactWithFilterUIModel(
    var contactId: String = String.EMPTY,
    var contactName: String = String.EMPTY,
    var photoUrl: String = String.EMPTY,
    var number: String = String.EMPTY,
    var phoneNumberValue: String = String.EMPTY,
    var isPhoneNumberValid: Boolean = false,
    var filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel()
) : Parcelable, NumberDataUIModel() {

    fun filterTypeIcon(): Int? {
        return if (isEmptyFilter()) null else filterWithFilteredNumberUIModel.filterTypeIcon()
    }
    fun isEmptyFilter(): Boolean {
        return filterWithFilteredNumberUIModel.filter.isEmpty()
    }

    fun placeHolder(context: Context): Drawable? {
        return if (contactName.nameInitial().isEmpty()) ContextCompat.getDrawable(context, R.drawable.ic_contact)
        else context.getInitialDrawable(contactName.nameInitial())
    }

    fun phoneNumberValidity(): Int? {
        return when {
            isPhoneNumberValid.not() -> R.string.details_number_invalid
            number.digitsTrimmed().startsWith(Constants.PLUS_CHAR).isNotTrue() -> R.string.details_number_incomplete
            else -> null
        }
    }
}
