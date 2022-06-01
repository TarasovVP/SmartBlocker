package com.tarasovvp.blacklister.ui.start.login

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentLoginBinding
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.start.GoogleFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener


class LoginFragment : GoogleFragment<FragmentLoginBinding, LoginViewModel>() {

    override fun getViewBinding() = FragmentLoginBinding.inflate(layoutInflater)

    override val viewModelClass = LoginViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnButtonsClick()
    }

    private fun setOnButtonsClick() {
        binding?.continueButton?.setSafeOnClickListener {
            if (binding?.email?.text.isNullOrEmpty() || binding?.password?.text.isNullOrEmpty()) {
                showMessage(getString(R.string.enter_login_password), true)
            } else {
                viewModel.signInWithEmailAndPassword(binding?.email?.text.toString(),
                    binding?.password?.text.toString())
            }
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

    override fun observeLiveData() {
        with(viewModel) {
            successSignInLiveData.safeSingleObserve(viewLifecycleOwner, {
                (activity as MainActivity).apply {
                    getAllData()
                }
                findNavController().navigate(R.id.callLogListFragment)
            })
            successPasswordResetLiveData.safeSingleObserve(viewLifecycleOwner, {
                showMessage(getString(R.string.password_reset_text), false)
            })
        }
    }
}