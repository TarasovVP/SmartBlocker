package com.example.blacklister.ui.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.blacklister.R
import com.example.blacklister.databinding.FragmentLoginBinding
import com.example.blacklister.extensions.safeSingleObserve
import com.example.blacklister.ui.base.BaseFragment
import com.example.blacklister.utils.PermissionUtil
import com.example.blacklister.utils.PermissionUtil.checkPermissions
import com.example.blacklister.utils.setSafeOnClickListener

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override fun getViewBinding() = FragmentLoginBinding.inflate(layoutInflater)

    override val viewModelClass = LoginViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(
            "loginViewModelTAG",
            "LoginFragment onViewCreated"
        )
        if (context?.checkPermissions() == true) {
            viewModel.getCallLogList()
        } else {
            requestPermissionLauncher.launch(PermissionUtil.permissionsArray())
        }
        binding?.loginNext?.setSafeOnClickListener {
            findNavController().navigate(R.id.startCallLogList)
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successLiveData.safeSingleObserve(viewLifecycleOwner, {
                Log.e(
                    "loginViewModelTAG",
                    "LoginFragment callLogLiveData.safeSingleObserve success $it"
                )
            })
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
                viewModel.getCallLogList()
            }
        }
}