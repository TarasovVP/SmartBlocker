package com.example.blacklister.ui.login

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.blacklister.R
import com.example.blacklister.databinding.FragmentLoginBinding
import com.example.blacklister.ui.base.BaseFragment
import com.example.blacklister.utils.setSafeOnClickListener

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override fun getViewBinding() = FragmentLoginBinding.inflate(layoutInflater)

    override val viewModelClass = LoginViewModel::class.java


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("attachTAG", "LoginFragment onViewCreated")
        binding?.loginNext?.setSafeOnClickListener {
            findNavController().navigate(R.id.startCallLogList)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e("attachTAG", "LoginFragment onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e("attachTAG", "LoginFragment onStop")
    }
    override fun observeLiveData() {

    }
}