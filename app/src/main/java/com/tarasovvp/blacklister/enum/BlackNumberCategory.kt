package com.tarasovvp.blacklister.enum

import com.tarasovvp.blacklister.R

enum class BlackNumberCategory(val id: Int, val title: Int) {
    OTHER(
        0,
        R.string.other
    ),
    PRIVATE(
        1,
        R.string.privates
    ),
    FINANCE(
        2,
        R.string.finance
    ),
    COLLECTOR(
        3,
        R.string.collectors
    ),
    SPAM(
        4,
        R.string.spam
    ),
    FRAUD(
        4,
        R.string.fraud
    )
}