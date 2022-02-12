package com.example.blacklister.ui.contactlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.blacklister.R
import com.example.blacklister.databinding.ContactListFragmentBinding
import com.example.blacklister.ui.base.BaseBindingFragment

class ContactListFragment : BaseBindingFragment<ContactListFragmentBinding>() {

    private lateinit var viewModel: ContactListViewModel

    override fun getViewBinding() = ContactListFragmentBinding.inflate(layoutInflater)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.contact_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}