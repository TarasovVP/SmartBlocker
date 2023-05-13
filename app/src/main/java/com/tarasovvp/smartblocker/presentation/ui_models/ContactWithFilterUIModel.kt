package com.tarasovvp.smartblocker.presentation.ui_models

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.getInitialDrawable
import com.tarasovvp.smartblocker.utils.extensions.isNotTrue
import com.tarasovvp.smartblocker.utils.extensions.nameInitial
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactWithFilterUIModel(
    var contactUIModel: ContactUIModel? = ContactUIModel(),
    var filterUIModel: FilterUIModel? = FilterUIModel()
) : Parcelable, NumberDataUIModel() {
    fun placeHolder(context: Context): Drawable? {
        return if (contactUIModel?.name.nameInitial().isEmpty()) ContextCompat.getDrawable(context,
            R.drawable.ic_contact) else context.getInitialDrawable(contactUIModel?.name.nameInitial())
    }

    fun phoneNumberValidity(): Int? {
        return when {
            contactUIModel?.isPhoneNumberValid.isNotTrue() -> R.string.details_number_invalid
            contactUIModel?.number?.startsWith(Constants.PLUS_CHAR).isNotTrue() -> R.string.details_number_incomplete
            else -> null
        }
    }
}
