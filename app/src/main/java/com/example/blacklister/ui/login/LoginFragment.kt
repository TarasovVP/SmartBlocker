package com.example.blacklister.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.blacklister.R
import com.example.blacklister.databinding.LoginFragmentBinding
import com.example.blacklister.ui.base.BaseBindingFragment

class LoginFragment : BaseBindingFragment<LoginFragmentBinding>() {

    private lateinit var viewModel: LoginViewModel

    override fun getViewBinding() = LoginFragmentBinding.inflate(layoutInflater)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_fragment, container, false)
        val button = view.findViewById<Button>(R.id.login_next)
        button.setOnClickListener {
            findNavController().navigate(R.id.fragment_log_list)
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

}