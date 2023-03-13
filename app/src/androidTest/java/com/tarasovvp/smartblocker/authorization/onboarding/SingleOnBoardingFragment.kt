package com.tarasovvp.smartblocker.ui.main.authorization.onboarding

import android.os.Bundle
import android.view.View
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSingleOnBoardingBinding
import com.tarasovvp.smartblocker.enums.OnBoarding
import com.tarasovvp.smartblocker.extensions.serializable
import com.tarasovvp.smartblocker.ui.base.BaseBindingFragment

class SingleOnBoardingFragment :
    BaseBindingFragment<FragmentSingleOnBoardingBinding>() {

    override var layoutId = R.layout.fragment_single_on_boarding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.onBoarding = arguments?.serializable(OnBoarding::class.java.simpleName) as OnBoarding?
        binding?.executePendingBindings()
    }

    companion object {
        @JvmStatic
        fun newInstance(onBoarding: OnBoarding): SingleOnBoardingFragment {
            return SingleOnBoardingFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(OnBoarding::class.java.simpleName, onBoarding)
                }
            }
        }
    }
}