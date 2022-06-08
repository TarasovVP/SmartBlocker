package com.tarasovvp.blacklister.ui.start.onboarding

import android.os.Bundle
import android.view.View
import com.tarasovvp.blacklister.databinding.FragmentSingleOnboardingBinding
import com.tarasovvp.blacklister.enum.OnBoarding
import com.tarasovvp.blacklister.ui.base.BaseBindingFragment

class SingleOnBoardingFragment(private val onBoarding: OnBoarding) :
    BaseBindingFragment<FragmentSingleOnboardingBinding>() {

    override fun getViewBinding() = FragmentSingleOnboardingBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.singleOnBoardingTitle?.text = getString(onBoarding.title)
        binding?.singleOnBoardingIcon?.setImageResource(onBoarding.icon)
    }
}