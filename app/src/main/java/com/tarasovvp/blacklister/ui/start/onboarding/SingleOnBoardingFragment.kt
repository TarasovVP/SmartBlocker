package com.tarasovvp.blacklister.ui.start.onboarding

import android.os.Bundle
import android.view.View
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentSingleOnBoardingBinding
import com.tarasovvp.blacklister.enums.OnBoarding
import com.tarasovvp.blacklister.ui.base.BaseBindingFragment

class SingleOnBoardingFragment(private val onBoarding: OnBoarding) :
    BaseBindingFragment<FragmentSingleOnBoardingBinding>() {

    override var layoutId = R.layout.fragment_single_on_boarding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.onBoarding = onBoarding
        binding?.executePendingBindings()
    }
}