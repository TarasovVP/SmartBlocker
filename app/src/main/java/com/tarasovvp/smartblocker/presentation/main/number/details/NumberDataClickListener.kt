package com.tarasovvp.smartblocker.presentation.main.number.details

import com.tarasovvp.smartblocker.domain.entities.models.NumberData

interface NumberDataClickListener {
    fun onNumberDataClick(numberData: NumberData)
}