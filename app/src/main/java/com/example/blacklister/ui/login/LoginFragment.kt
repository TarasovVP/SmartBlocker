package com.example.blacklister.ui.login

import com.example.blacklister.databinding.LoginFragmentBinding
import com.example.blacklister.ui.base.BaseFragment

class LoginFragment : BaseFragment<LoginFragmentBinding, LoginViewModel>() {

    override fun getViewBinding() = LoginFragmentBinding.inflate(layoutInflater)

    override val viewModelClass = LoginViewModel::class.java

}