package com.example.blacklister.ui.contactlist

import android.Manifest.permission.READ_CONTACTS
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.blacklister.databinding.ContactListFragmentBinding
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
            ContactAdapter()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.contactListRecyclerView?.initRecyclerView()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(READ_CONTACTS),
                READ_CONTACTS_REQUEST_CODE
            )
        } else {
            viewModel.getContactList()
        }
        with(viewModel) {
            contactLiveData.observe(viewLifecycleOwner, {
                onInitialDataLoaded(it)
                Log.e("contactTAG", "contactList ${Gson().toJson(it)} adapter?.all?.size ${adapter?.all?.size}")
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
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.getContactList()
        }
    }

    companion object {
        const val READ_CONTACTS_REQUEST_CODE = 500
    }
}