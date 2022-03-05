package com.example.blacklister.ui.contactdetail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.blacklister.R
import com.example.blacklister.databinding.FragmentContactDetailBinding
import com.example.blacklister.ui.base.BaseFragment
import com.example.blacklister.utils.setSafeOnClickListener

class ContactDetailFragment : BaseFragment<FragmentContactDetailBinding, ContactDetailViewModel>() {

    override fun getViewBinding() = FragmentContactDetailBinding.inflate(layoutInflater)

    override val viewModelClass = ContactDetailViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.loginNext?.setSafeOnClickListener {
            findNavController().navigate(R.id.fragment_log_list)
        }
    }

}