package com.example.blacklister.ui.login

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.blacklister.R
import com.example.blacklister.databinding.FragmentLoginBinding
import com.example.blacklister.extensions.isPermissionAccepted
import com.example.blacklister.ui.base.BaseFragment
import com.example.blacklister.utils.setSafeOnClickListener

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override fun getViewBinding() = FragmentLoginBinding.inflate(layoutInflater)

    override val viewModelClass = LoginViewModel::class.java

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, @JvmSuppressWildcards Boolean>? ->
            if (isGranted?.values?.contains(false) == true) {
                Toast.makeText(
                    context,
                    getString(R.string.glve_all_permissions),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                findNavController().navigate(R.id.startCallLogList)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.loginNext?.setSafeOnClickListener {
            checkPermissions()
        }
    }

    private fun checkPermissions() {
        if (context?.isPermissionAccepted(Manifest.permission.READ_CONTACTS) != true || context?.isPermissionAccepted(Manifest.permission.WRITE_CALL_LOG) != true || context?.isPermissionAccepted(
                Manifest.permission.READ_CALL_LOG
            ) != true || context?.isPermissionAccepted(
                Manifest.permission.ANSWER_PHONE_CALLS
            ) != true || context?.isPermissionAccepted(Manifest.permission.READ_PHONE_STATE) != true || context?.isPermissionAccepted(
                Manifest.permission.CALL_PHONE
            ) != true
        ) {
            val permissionsArray =
                arrayListOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CALL_LOG,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE
                )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                permissionsArray.add(Manifest.permission.ANSWER_PHONE_CALLS)
            }
            requestPermissionLauncher.launch(permissionsArray.toTypedArray())
        } else {
            findNavController().navigate(R.id.startCallLogList)
        }
    }

    override fun observeLiveData() {

    }
}