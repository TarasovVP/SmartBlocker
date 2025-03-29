package com.tarasovvp.smartblocker.utils

import androidx.navigation.NavController
import com.tarasovvp.smartblocker.R

object BackPressedUtil {
    private fun backPressedArray(): Array<Int> = arrayOf(R.id.onBoardingFragment, R.id.loginFragment, R.id.listBlockerFragment)

    fun NavController.isBackPressedScreen(): Boolean {
        return backPressedArray().contains(this.currentDestination?.id)
    }
}
