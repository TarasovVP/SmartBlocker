package com.tarasovvp.blacklister.ui.start.signup

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.gson.Gson
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.databinding.FragmentSignUpBinding
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.start.GoogleFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener


class SignUpFragment : GoogleFragment<FragmentSignUpBinding, SignUpViewModel>() {

    override fun getViewBinding() = FragmentSignUpBinding.inflate(layoutInflater)

    override val viewModelClass = SignUpViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("signUserTAG", "SignUpFragment onViewCreated BlackListerApp.instance?.auth?.currentUser?.displayName ${Gson().toJson(BlackListerApp.instance?.auth?.currentUser?.displayName)}")
        binding?.googleAuth?.setSafeOnClickListener {
            googleSignInLauncher.launch(googleSignInClient?.signInIntent)
        }
        binding?.continueButton?.setSafeOnClickListener {
            createUserWithEmailAndPassword(binding?.email?.text.toString(), binding?.password?.text.toString(), binding?.name?.text.toString())
        }
    }

    private fun createUserWithEmailAndPassword(email: String, password: String, name: String) {
        activity?.let {
            BlackListerApp.instance?.auth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(
                it) { createUserTask ->
                if (createUserTask.isSuccessful) {
                    findNavController().popBackStack()
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name).build()

                    createUserTask.result.user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateUserTask ->
                            if (updateUserTask.isSuccessful) {
                                Log.e("signUserTAG",
                                    "SignUpFragment createUserWithEmailAndPassword BlackListerApp.instance?.auth?.currentUser ${
                                        Gson().toJson(BlackListerApp.instance?.auth?.currentUser?.displayName)
                                    }")
                            }
                        }

                    Toast.makeText(context, "Success. Please log in", Toast.LENGTH_LONG).show()
                    Log.e("signUserTAG", "SignUpFragment createUserWithEmailAndPassword createUserTask.isSuccessful ${createUserTask.isSuccessful}")
                } else {
                    Toast.makeText(context, createUserTask.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                    Log.e("signUserTAG", "SignUpFragment createUserWithEmailAndPassword createUserTask.exception ${createUserTask.exception}")
                }
            }
        }
    }

    override fun firebaseAuthWithGoogle(idToken: String) {
        viewModel.firebaseAuthWithGoogle(idToken)
    }

    override fun observeLiveData() {
        with(viewModel) {
            successSignInLiveData.safeSingleObserve(viewLifecycleOwner, {
                Log.e("signUserTAG", "SignUpFragment observeLiveData successSignInLiveData")
            })
            exceptionLiveData.safeSingleObserve(viewLifecycleOwner, { exception ->
                Log.e("signUserTAG",
                    "SignUpFragment observeLiveData exceptionLiveData exception $exception")
            })
        }
    }

}