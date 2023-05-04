package com.tarasovvp.smartblocker.domain.models.entities

import android.os.Parcelable
import androidx.room.*
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTERS
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(tableName = FILTERS)
@Parcelize
data class Filter(
    @PrimaryKey var filter: String = String.EMPTY,
    var conditionType: Int = DEFAULT_FILTER,
    var filterType: Int = DEFAULT_FILTER,
    var name: String? = String.EMPTY,
    var countryCode: String = String.EMPTY,
    var country: String = String.EMPTY,
    var filterWithoutCountryCode: String = String.EMPTY,
    var created: Long? = null
) : Parcelable {

    @IgnoredOnParcel
    @get:Exclude
    var filteredContacts: Int = 0

    @IgnoredOnParcel
    @get:Exclude
    var filteredCalls: Int = 0

    @Exclude
    fun isTypeStart(): Boolean {
        return conditionType == FilterCondition.FILTER_CONDITION_START.ordinal
    }

    @Exclude
    fun isTypeFull(): Boolean {
        return conditionType == FilterCondition.FILTER_CONDITION_FULL.ordinal
    }

    @Exclude
    fun isTypeContain(): Boolean {
        return conditionType == FilterCondition.FILTER_CONDITION_CONTAIN.ordinal
    }

    @Exclude
    fun isBlocker(): Boolean {
        return filterType == Constants.BLOCKER
    }

    @Exclude
    fun isPermission(): Boolean {
        return filterType == Constants.PERMISSION
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Filter) {
            this.filter == other.filter && this.conditionType == other.conditionType && this.filterType == other.filterType && this.filteredContacts == other.filteredContacts
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = filter.hashCode()
        result = 31 * result + conditionType
        result = 31 * result + filterType
        result = 31 * result + filteredContacts
        return result
    }
}