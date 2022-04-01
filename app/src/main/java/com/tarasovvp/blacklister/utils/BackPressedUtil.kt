package com.tarasovvp.blacklister.utils

import androidx.navigation.NavController
import com.tarasovvp.blacklister.R

object BackPressedUtil {

    private fun backPressedArray(): Array<Int> = arrayOf(R.id.onBoardingFragment, R.id.loginFragment, R.id.callLogListFragment)

    fun NavController.isBackPressedScreen(): Boolean {
        return backPressedArray().contains(this.currentDestination?.id)
    }
}