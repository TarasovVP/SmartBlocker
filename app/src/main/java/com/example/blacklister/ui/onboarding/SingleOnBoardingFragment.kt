package com.example.blacklister.ui.onboarding

import android.os.Bundle
import android.view.View
import com.example.blacklister.databinding.FragmentSingleOnboardingBinding
import com.example.blacklister.enum.OnBoarding
import com.example.blacklister.ui.base.BaseFragment

class SingleOnBoardingFragment(private val onBoarding: OnBoarding) :
    BaseFragment<FragmentSingleOnboardingBinding, OnBoardingViewModel>() {

    override fun getViewBinding() = FragmentSingleOnboardingBinding.inflate(layoutInflater)

    override val viewModelClass = OnBoardingViewModel::class.java

    override fun observeLiveData() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.singleOnBoardingTitle?.text = getString(onBoarding.title)
        binding?.singleOnBoardingIcon?.setImageResource(onBoarding.icon)
    }

}