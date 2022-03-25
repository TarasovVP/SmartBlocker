package com.example.blacklister.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.blacklister.databinding.FragmentSingleOnboardingBinding
import com.example.blacklister.enum.OnBoarding

class SingleOnBoardingFragment(private val onBoarding: OnBoarding) : Fragment() {

    private var binding: FragmentSingleOnboardingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSingleOnboardingBinding.inflate(inflater)
        binding?.singleOnBoardingTitle?.text = getString(onBoarding.title)
        binding?.singleOnBoardingIcon?.setImageResource(onBoarding.icon)
        return binding?.root
    }
}