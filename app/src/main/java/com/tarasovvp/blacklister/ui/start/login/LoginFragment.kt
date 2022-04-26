package com.tarasovvp.blacklister.ui.start.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.BlackListerApp
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
        Log.e("signUserTAG",
            "LoginFragment onViewCreated currentUser.email ${BlackListerApp.instance?.auth?.currentUser?.email}")
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
                Toast.makeText(
                    context,
                    getString(R.string.give_all_permissions),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                (activity as MainActivity).getAllData()
            }
        }

    override fun firebaseAuthWithGoogle(idToken: String) {
        viewModel.firebaseAuthWithGoogle(idToken)
    }

    override fun observeLiveData() {
        with(viewModel) {
            successSignInLiveData.safeSingleObserve(viewLifecycleOwner, {
                Log.e("signUserTAG", "LoginFragment observeLiveData successSignInLiveData")
                findNavController().navigate(R.id.callLogListFragment)
            })
            exceptionLiveData.safeSingleObserve(viewLifecycleOwner, { exception ->
                Toast.makeText(context, exception, Toast.LENGTH_LONG).show()
                Log.e("signUserTAG",
                    "LoginFragment observeLiveData exceptionLiveData exception $exception")
            })
        }
    }
}