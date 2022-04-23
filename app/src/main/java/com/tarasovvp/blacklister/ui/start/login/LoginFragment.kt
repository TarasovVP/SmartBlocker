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


class LoginFragment : GoogleFragment<FragmentLoginBinding, ViewModel>() {

    override fun getViewBinding() = FragmentLoginBinding.inflate(layoutInflater)

    override val viewModelClass = ViewModel::class.java

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
            signInWithEmailAndPassword(binding?.email?.text.toString(),
                binding?.password?.text.toString())
        }
        binding?.continueWithoutAccButton?.setSafeOnClickListener {
            findNavController().navigate(R.id.startCallLogList)
        }
        binding?.register?.setSafeOnClickListener {
            findNavController().navigate(R.id.startSignUpFragment)
        }
        binding?.buttonForgotPassword?.setSafeOnClickListener {
            sendPasswordResetEmail(binding?.email?.text.toString())
        }
        binding?.googleAuth?.setSafeOnClickListener {
            googleSignInLauncher.launch(googleSignInClient?.signInIntent)
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        activity?.let {
            BlackListerApp.instance?.auth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(
                    it) { task ->
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.callLogListFragment)
                        Log.e("signUserTAG",
                            "LoginFragment signInWithEmailAndPassword task.isSuccessful ${task.isSuccessful} currentUser.email ${BlackListerApp.instance?.auth?.currentUser?.email}")
                    } else {
                        Toast.makeText(context, task.exception?.localizedMessage, Toast.LENGTH_LONG)
                            .show()
                        Log.e("signUserTAG",
                            "LoginFragment signInWithEmailAndPassword task.exception ${task.exception}")
                    }
                }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        activity?.let {
            BlackListerApp.instance?.auth?.sendPasswordResetEmail(email)?.addOnCompleteListener(
                it) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Check your email", Toast.LENGTH_LONG).show()
                    Log.e("signUserTAG",
                        "LoginFragment signInWithEmailAndPassword task.isSuccessful ${task.isSuccessful} currentUser.email ${BlackListerApp.instance?.auth?.currentUser?.email}")
                } else {
                    Toast.makeText(context, task.exception?.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                    Log.e("signUserTAG",
                        "LoginFragment signInWithEmailAndPassword task.exception ${task.exception}")
                }
            }
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
            })
            exceptionLiveData.safeSingleObserve(viewLifecycleOwner, { exception ->
                Log.e("signUserTAG",
                    "LoginFragment observeLiveData exceptionLiveData exception $exception")
            })
        }
    }
}