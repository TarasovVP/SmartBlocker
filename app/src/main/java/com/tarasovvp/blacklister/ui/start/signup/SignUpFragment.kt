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
            viewModel.createUserWithEmailAndPassword(binding?.email?.text.toString(), binding?.password?.text.toString(), binding?.name?.text.toString())
        }
    }

    override fun firebaseAuthWithGoogle(idToken: String) {
        viewModel.firebaseAuthWithGoogle(idToken)
    }

    override fun observeLiveData() {
        with(viewModel) {
            successSignInLiveData.safeSingleObserve(viewLifecycleOwner, {
                Toast.makeText(context, "Success. Please log in", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
                Log.e("signUserTAG", "SignUpFragment observeLiveData successSignInLiveData")
            })
            exceptionLiveData.safeSingleObserve(viewLifecycleOwner, { exception ->
                Toast.makeText(context, exception, Toast.LENGTH_LONG).show()
                Log.e("signUserTAG",
                    "SignUpFragment observeLiveData exceptionLiveData exception $exception")
            })
        }
    }

}