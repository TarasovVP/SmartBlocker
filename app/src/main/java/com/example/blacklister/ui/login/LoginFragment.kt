package com.example.blacklister.ui.login

import android.os.Bundle
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
        binding?.loginNext?.setSafeOnClickListener {
            findNavController().navigate(R.id.fragment_log_list)
        }
    }

}