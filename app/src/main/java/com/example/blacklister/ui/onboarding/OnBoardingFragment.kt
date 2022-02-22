package com.example.blacklister.ui.onboarding

import com.example.blacklister.databinding.OnboardingFragmentBinding
import com.example.blacklister.ui.base.BaseFragment

class OnBoardingFragment : BaseFragment<OnboardingFragmentBinding, OnboardingViewModel>() {

    override fun getViewBinding() = OnboardingFragmentBinding.inflate(layoutInflater)

    override val viewModelClass = OnboardingViewModel::class.java

}