package com.tarasovvp.smartblocker.presentation.ui_models

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.presentation.ui_models.NumberData
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.getInitialDrawable
import com.tarasovvp.smartblocker.utils.extensions.isNotTrue
import com.tarasovvp.smartblocker.utils.extensions.nameInitial
import kotlinx.parcelize.Parcelize

@DatabaseView("SELECT contacts.*, filters.* FROM contacts " +
        "LEFT JOIN filters ON (filters.filter = contacts.phoneNumberValue AND filters.conditionType = 0) OR (contacts.phoneNumberValue LIKE filters.filter || '%' AND filters.conditionType = 1) OR (contacts.phoneNumberValue LIKE '%' || filters.filter || '%' AND filters.conditionType = 2) " +
        "WHERE filters.filter = (SELECT filter FROM filters WHERE contacts.phoneNumberValue LIKE filter || '%' OR contacts.phoneNumberValue LIKE '%' || filter || '%' ORDER BY LENGTH(filter) DESC LIMIT 1) OR filters.filter IS NULL")
@Parcelize
data class ContactWithFilterUIModel(
    @Embedded
    var contact: Contact? = Contact(),
    @Embedded
    var filterWithCountryCode: FilterWithCountryCode? = FilterWithCountryCode()
) : Parcelable, NumberData() {
    fun placeHolder(context: Context): Drawable? {
        return if (contact?.name.nameInitial().isEmpty()) ContextCompat.getDrawable(context,
            R.drawable.ic_contact) else context.getInitialDrawable(contact?.name.nameInitial())
    }

    fun phoneNumberValidity(): Int? {
        return when {
            contact?.isPhoneNumberValid.isNotTrue() -> R.string.details_number_invalid
            contact?.number?.startsWith(Constants.PLUS_CHAR).isNotTrue() -> R.string.details_number_incomplete
            else -> null
        }
    }
}
