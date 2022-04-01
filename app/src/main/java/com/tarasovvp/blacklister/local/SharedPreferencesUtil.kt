package com.tarasovvp.blacklister.local

import com.tarasovvp.blacklister.local.Settings.ON_BOARDING_SEEN
import com.tarasovvp.blacklister.local.Settings.loadBoolean
import com.tarasovvp.blacklister.local.Settings.saveBoolean

object SharedPreferencesUtil {

    var isOnBoardingSeen: Boolean
        get() = loadBoolean(ON_BOARDING_SEEN)
        set(isOnBoardingSeen) {
            saveBoolean(
                ON_BOARDING_SEEN,
                isOnBoardingSeen
            )
        }
}