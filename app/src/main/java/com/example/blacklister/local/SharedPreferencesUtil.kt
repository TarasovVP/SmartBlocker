package com.example.blacklister.local

import com.example.blacklister.local.Settings.ON_BOARDING_SEEN
import com.example.blacklister.local.Settings.loadBoolean
import com.example.blacklister.local.Settings.saveBoolean

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