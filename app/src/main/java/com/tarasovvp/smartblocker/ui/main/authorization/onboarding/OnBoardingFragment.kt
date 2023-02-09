package com.tarasovvp.smartblocker.ui.main.authorization.onboarding

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.ACCEPT_PERMISSIONS_SCREEN
import com.tarasovvp.smartblocker.databinding.FragmentOnBoardingBinding
import com.tarasovvp.smartblocker.enums.OnBoarding
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.orZero
import com.tarasovvp.smartblocker.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import com.tarasovvp.smartblocker.ui.base.BaseBindingFragment
import com.tarasovvp.smartblocker.utils.PermissionUtil.checkPermissions
import com.tarasovvp.smartblocker.utils.PermissionUtil.permissionsArray

class OnBoardingFragment : BaseBindingFragment<FragmentOnBoardingBinding>() {

    override var layoutId = R.layout.fragment_on_boarding
    private var currentPosition = 0

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, @JvmSuppressWildcards Boolean>? ->
            if (isGranted?.values?.contains(false).isTrue()) {
                showMessage(getString(R.string.app_need_permissions), true)
            } else {
                startLoginScreen()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.onBoardingButton?.setSafeOnClickListener {
            if (currentPosition == ACCEPT_PERMISSIONS_SCREEN) {
                checkPermissions()
            } else {
                binding?.onBoardingViewPager?.setCurrentItem((binding?.onBoardingViewPager?.currentItem.orZero()) + 1,
                    false)
            }
        }
        initViewPager()
    }

    private fun initViewPager() {
        val fragmentList = arrayListOf(
            SingleOnBoardingFragment.newInstance(OnBoarding.INNER_DATA_ACCESS),
            SingleOnBoardingFragment.newInstance(OnBoarding.BLACK_WHITE_LIST),
            SingleOnBoardingFragment.newInstance(OnBoarding.AUTHORIZED_USER),
            SingleOnBoardingFragment.newInstance(OnBoarding.ACCESS_PERMISSIONS)
        )

        val adapter = activity?.supportFragmentManager?.let { fragmentManager ->
            OnBoardingAdapter(
                fragmentList,
                fragmentManager,
                lifecycle
            )
        }

        binding?.onBoardingViewPager?.adapter = adapter
        binding?.onBoardingViewPager?.isUserInputEnabled = false
        binding?.onBoardingViewPager?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
                binding?.onBoardingButton?.text =
                    if (position == ACCEPT_PERMISSIONS_SCREEN) getString(R.string.button_accept) else getString(
                        R.string.button_next)
            }
        })
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