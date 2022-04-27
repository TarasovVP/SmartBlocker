package com.tarasovvp.blacklister.ui.start.login

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentLoginBinding
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.start.GoogleFragment
import com.tarasovvp.blacklister.utils.PermissionUtil
import com.tarasovvp.blacklister.utils.PermissionUtil.checkPermissions
import com.tarasovvp.blacklister.utils.setSafeOnClickListener


class LoginFragment : GoogleFragment<FragmentLoginBinding, LoginViewModel>() {

    override fun getViewBinding() = FragmentLoginBinding.inflate(layoutInflater)

    override val viewModelClass = LoginViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (context?.checkPermissions() == true) {
            (activity as MainActivity).getAllData()
        } else {
            requestPermissionLauncher.launch(PermissionUtil.permissionsArray())
        }
        setOnButtonsClick()
    }

    private fun setOnButtonsClick() {
        binding?.continueButton?.setSafeOnClickListener {
            viewModel.signInWithEmailAndPassword(binding?.email?.text.toString(),
                binding?.password?.text.toString())
        }
        binding?.continueWithoutAccButton?.setSafeOnClickListener {
            findNavController().navigate(R.id.startCallLogList)
        }
        binding?.register?.setSafeOnClickListener {
            findNavController().navigate(R.id.startSignUpFragment)
        }
        binding?.buttonForgotPassword?.setSafeOnClickListener {
            viewModel.sendPasswordResetEmail(binding?.email?.text.toString())
        }
        binding?.googleAuth?.setSafeOnClickListener {
            googleSignInLauncher.launch(googleSignInClient?.signInIntent)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, @JvmSuppressWildcards Boolean>? ->
            if (isGranted?.values?.contains(false) == true) {
                showMessage(getString(R.string.give_all_permissions))
            } else {
                (activity as MainActivity).getAllData()
            }
        }

    override fun observeLiveData() {
        with(viewModel) {
            successSignInLiveData.safeSingleObserve(viewLifecycleOwner, {
                findNavController().navigate(R.id.callLogListFragment)
            })
            successPasswordResetLiveData.safeSingleObserve(viewLifecycleOwner, {
                showMessage(getString(R.string.password_reset_text))
            })
        }
    }
}