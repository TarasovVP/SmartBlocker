package com.tarasovvp.smartblocker

import com.tarasovvp.smartblocker.ui.main.authorization.sign_up.SignUpFragment
import com.tarasovvp.smartblocker.ui.main.authorization.sign_up.SignUpViewModel
import org.junit.Test

import org.junit.Assert.*

class SignUpUnitTest {

    @Test
    fun testLayoutIdAndViewModelClass() {
        val fragment = SignUpFragment()
        assertEquals(R.layout.fragment_sign_up, fragment.layoutId)
        assertEquals(SignUpViewModel::class.java, fragment.viewModelClass)
    }
}