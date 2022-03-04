package com.example.blacklister.ui.onboarding

import com.example.blacklister.databinding.FragmentOnboardingBinding
import com.example.blacklister.ui.base.BaseFragment

class OnBoardingFragment : BaseFragment<FragmentOnboardingBinding, OnboardingViewModel>() {

    override fun getViewBinding() = FragmentOnboardingBinding.inflate(layoutInflater)

    override val viewModelClass = OnboardingViewModel::class.java

}