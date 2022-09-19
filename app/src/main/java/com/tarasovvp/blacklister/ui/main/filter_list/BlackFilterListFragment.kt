package com.tarasovvp.blacklister.ui.main.filter_list

import android.util.Log
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.ui.MainActivity

class BlackFilterListFragment : BaseFilterListFragment() {
    override fun onStart() {
        super.onStart()
        (activity as MainActivity).apply {
            Log.e("blockerTAG", "FilterListFragment this $this SharedPreferencesUtil.blockTurnOff.not() && isBlockerLaunched().not() ${SharedPreferencesUtil.blockTurnOff.not() && isBlockerLaunched().not()}")
            if (SharedPreferencesUtil.blockTurnOff.not() && isBlockerLaunched().not()) startBlocker()
        }
    }
}
