package com.tarasovvp.blacklister.ui.signup

import android.os.Bundle
import android.util.Log
import android.view.View
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.databinding.FragmentSignUpBinding
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class SignUpFragment : BaseFragment<FragmentSignUpBinding, SignUpViewModel>() {

    override fun getViewBinding() = FragmentSignUpBinding.inflate(layoutInflater)

    override val viewModelClass = SignUpViewModel::class.java

    override fun observeLiveData() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.continueButton?.setSafeOnClickListener {
            createUserWithEmailAndPassword(binding?.email?.text.toString(), binding?.password?.text.toString())
        }
    }

    private fun createUserWithEmailAndPassword(email: String, password: String) {
        activity?.let {
            BlackListerApp.instance?.auth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(
                it) { task ->
                if (task.isSuccessful) {
                    Log.e("signUserTAG", "SignUpFragment createUserWithEmailAndPassword task.isSuccessful ${task.isSuccessful}")
                } else {
                    Log.e("signUserTAG", "SignUpFragment createUserWithEmailAndPassword task.exception ${task.exception}")
                }
            }
        }
    }

}