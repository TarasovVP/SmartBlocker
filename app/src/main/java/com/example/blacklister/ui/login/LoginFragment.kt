package com.example.blacklister.ui.login

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.blacklister.R
import com.example.blacklister.databinding.FragmentLoginBinding
import com.example.blacklister.extensions.isPermissionAccepted
import com.example.blacklister.ui.base.BaseFragment
import com.example.blacklister.ui.contactlist.ContactListFragment
import com.example.blacklister.utils.setSafeOnClickListener

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override fun getViewBinding() = FragmentLoginBinding.inflate(layoutInflater)

    override val viewModelClass = LoginViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.loginNext?.setSafeOnClickListener {
            checkPermissions()
        }
    }

    fun checkPermissions() {
        if (context?.isPermissionAccepted(Manifest.permission.READ_CONTACTS) != true || context?.isPermissionAccepted(
                Manifest.permission.READ_CALL_LOG
            ) != true || context?.isPermissionAccepted(
                Manifest.permission.ANSWER_PHONE_CALLS
            ) != true
        ) {
            val permissionsArray =
                arrayListOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_CALL_LOG
                )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                permissionsArray.add(Manifest.permission.ANSWER_PHONE_CALLS)
            }
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsArray.toTypedArray(),
                READ_CONTACTS_REQUEST_CODE
            )
        } else {
            findNavController().navigate(R.id.startLogList)
        }
    }

    override fun observeLiveData() {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode != READ_CONTACTS_REQUEST_CODE) {
            return
        }
        if (context?.isPermissionAccepted(Manifest.permission.READ_CONTACTS) == true && context?.isPermissionAccepted(
                Manifest.permission.READ_CALL_LOG
            ) == true && context?.isPermissionAccepted(
                Manifest.permission.ANSWER_PHONE_CALLS
            ) == true
        ) {
            findNavController().navigate(R.id.startLogList)
        } else {
            Toast.makeText(
                context,
                "To continue - give all necessary permissions",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        const val READ_CONTACTS_REQUEST_CODE = 500
    }

}