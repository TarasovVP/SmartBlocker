package com.tarasovvp.smartblocker.ui.authorization.onboarding

import android.os.Bundle
import android.view.View
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSingleOnBoardingBinding
import com.tarasovvp.smartblocker.enums.OnBoarding
import com.tarasovvp.smartblocker.ui.base.BaseBindingFragment

class SingleOnBoardingFragment(private val onBoarding: OnBoarding) :
    BaseBindingFragment<FragmentSingleOnBoardingBinding>() {

    override var layoutId = R.layout.fragment_single_on_boarding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.onBoarding = onBoarding
        binding?.executePendingBindings()
    }
}