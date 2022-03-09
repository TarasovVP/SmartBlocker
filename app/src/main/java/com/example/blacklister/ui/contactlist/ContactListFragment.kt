package com.example.blacklister.ui.contactlist

import android.Manifest.permission.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.blacklister.databinding.ContactListFragmentBinding
import com.example.blacklister.extensions.isPermissionAccepted
import com.example.blacklister.model.Contact
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseListFragment
import com.google.gson.Gson

class ContactListFragment :
    BaseListFragment<ContactListFragmentBinding, ContactListViewModel, Contact>() {

    override fun getViewBinding() = ContactListFragmentBinding.inflate(layoutInflater)

    override val viewModelClass = ContactListViewModel::class.java

    override fun createAdapter(): BaseAdapter<Contact, *>? {
        return context?.let {
            ContactAdapter(object : ContactClickListener {
                override fun onContactClicked(contact: Contact) {
                    findNavController().navigate(ContactListFragmentDirections.startContactDetail(contact = contact))
                }

            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.contactListRecyclerView?.initRecyclerView()
        if (context?.isPermissionAccepted(READ_CONTACTS) != true || context?.isPermissionAccepted(
                READ_PHONE_STATE
            ) != true || context?.isPermissionAccepted(CALL_PHONE) != true || context?.isPermissionAccepted(
                READ_CALL_LOG
            ) != true || context?.isPermissionAccepted(
                ANSWER_PHONE_CALLS
            ) != true
        ) {
            val permissionsArray =
                arrayListOf(READ_CONTACTS, READ_PHONE_STATE, CALL_PHONE, READ_CALL_LOG)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                permissionsArray.add(ANSWER_PHONE_CALLS)
            }
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsArray.toTypedArray(),
                READ_CONTACTS_REQUEST_CODE
            )
        } else {
            if (adapter?.all.isNullOrEmpty()) {
                viewModel.getContactList()
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            contactLiveData?.observe(viewLifecycleOwner, {
                onInitialDataLoaded(it)
            })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode != READ_CONTACTS_REQUEST_CODE) {
            return
        }
        if (context?.isPermissionAccepted(READ_CONTACTS) == true && context?.isPermissionAccepted(
                READ_PHONE_STATE
            ) == true && context?.isPermissionAccepted(CALL_PHONE) == true && context?.isPermissionAccepted(
                READ_CALL_LOG
            ) == true && context?.isPermissionAccepted(
                ANSWER_PHONE_CALLS
            ) == true
        ) {
            if (adapter?.all.isNullOrEmpty()) {
                viewModel.getContactList()
            }
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