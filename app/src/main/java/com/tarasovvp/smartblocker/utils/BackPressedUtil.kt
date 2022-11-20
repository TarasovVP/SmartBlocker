package com.tarasovvp.smartblocker.utils

import androidx.navigation.NavController
import com.tarasovvp.smartblocker.R

object BackPressedUtil {

    //TODO back pressed with navigation

    private fun backPressedArray(): Array<Int> =
        arrayOf(R.id.onBoardingFragment, R.id.loginFragment, R.id.blackFilterListFragment)

    fun NavController.isBackPressedScreen(): Boolean {
        return backPressedArray().contains(this.currentDestination?.id)
    }
}