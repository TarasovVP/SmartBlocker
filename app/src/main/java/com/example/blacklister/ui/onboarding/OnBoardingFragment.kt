package com.example.blacklister.ui.onboarding

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.blacklister.R
import com.example.blacklister.databinding.FragmentOnboardingBinding
import com.example.blacklister.enum.OnBoarding
import com.example.blacklister.extensions.isPermissionAccepted
import com.example.blacklister.local.SharedPreferencesUtil
import com.example.blacklister.ui.base.BaseFragment
import com.example.blacklister.utils.setSafeOnClickListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

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
                    getString(R.string.glve_all_permissions),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                findNavController().navigate(R.id.startLoginScreen)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.onBoardingButton?.setSafeOnClickListener {
            if (currentPosition == 2) {
                checkPermissions()
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
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                }.attach()
            }
                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        currentPosition = position
                        Log.e(
                            "tabTAG",
                            "position $position SharedPreferencesUtil.isOnBoardingSeen ${SharedPreferencesUtil.isOnBoardingSeen}"
                        )
                    }
                })
        }
    }

    private fun checkPermissions() {
        if (context?.isPermissionAccepted(Manifest.permission.READ_CONTACTS) != true || context?.isPermissionAccepted(
                Manifest.permission.WRITE_CALL_LOG) != true || context?.isPermissionAccepted(
                Manifest.permission.READ_CALL_LOG
            ) != true || context?.isPermissionAccepted(
                Manifest.permission.ANSWER_PHONE_CALLS
            ) != true || context?.isPermissionAccepted(Manifest.permission.READ_PHONE_STATE) != true || context?.isPermissionAccepted(
                Manifest.permission.CALL_PHONE
            ) != true
        ) {
            val permissionsArray =
                arrayListOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CALL_LOG,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE
                )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                permissionsArray.add(Manifest.permission.ANSWER_PHONE_CALLS)
            }
            requestPermissionLauncher.launch(permissionsArray.toTypedArray())
        } else {
            findNavController().navigate(R.id.startLoginScreen)
        }
    }

}