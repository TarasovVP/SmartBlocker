package com.tarasovvp.blacklister.ui.onboarding

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.ACCEPT_PERMISSIONS_SCREEN
import com.tarasovvp.blacklister.databinding.FragmentOnboardingBinding
import com.tarasovvp.blacklister.enum.OnBoarding
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.PermissionUtil.checkPermissions
import com.tarasovvp.blacklister.utils.PermissionUtil.permissionsArray
import com.google.android.material.tabs.TabLayoutMediator
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class OnBoardingFragment : BaseFragment<FragmentOnboardingBinding, OnBoardingViewModel>() {

    override fun getViewBinding() = FragmentOnboardingBinding.inflate(layoutInflater)

    override val viewModelClass = OnBoardingViewModel::class.java

    private var currentPosition = 0

    override fun observeLiveData() {

    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, @JvmSuppressWildcards Boolean>? ->
            if (isGranted?.values?.contains(false) == true) {
                Toast.makeText(
                    context,
                    getString(R.string.give_all_permissions),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                startLoginScreen()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(
            "loginViewModelTAG",
            "OnBoardingFragment onViewCreated"
        )
        binding?.onBoardingButton?.setSafeOnClickListener {
            if (currentPosition == ACCEPT_PERMISSIONS_SCREEN) {
                checkPermissions()
            } else {
                binding?.onBoardingViewPager?.currentItem =
                    (binding?.onBoardingViewPager?.currentItem ?: 0) + 1
            }
        }
        initViewPager()
    }

    private fun initViewPager() {
        val fragmentList = arrayListOf(
            SingleOnBoardingFragment(OnBoarding.AVOID_CALL),
            SingleOnBoardingFragment(OnBoarding.RECEIVE_NOTIFICATIONS),
            SingleOnBoardingFragment(OnBoarding.ACCEPT_PERMISSIONS)
        )

        val adapter = activity?.supportFragmentManager?.let { fragmentManager ->
            OnBoardingAdapter(
                fragmentList,
                fragmentManager,
                lifecycle
            )
        }

        binding?.onBoardingViewPager?.adapter = adapter
        binding?.onBoardingViewPager?.let { viewPager ->
            binding?.onBoardingTabLayout?.let { tabLayout ->
                TabLayoutMediator(tabLayout, viewPager) { _, _ ->
                }.attach()
            }
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentPosition = position
                    binding?.onBoardingButton?.text =
                        if (position == ACCEPT_PERMISSIONS_SCREEN) getString(R.string.accept) else getString(
                            R.string.next
                        )
                }
            })
        }
    }

    private fun checkPermissions() {
        if (context?.checkPermissions() != true) {
            requestPermissionLauncher.launch(permissionsArray())
        } else {
            startLoginScreen()
        }
    }

    private fun startLoginScreen() {
        SharedPreferencesUtil.isOnBoardingSeen = true
        findNavController().navigate(R.id.startLoginScreen)
    }

}