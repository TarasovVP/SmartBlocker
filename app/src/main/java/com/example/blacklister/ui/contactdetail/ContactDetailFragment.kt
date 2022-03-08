package com.example.blacklister.ui.contactdetail

import com.example.blacklister.databinding.FragmentContactDetailBinding
import com.example.blacklister.ui.base.BaseFragment

class ContactDetailFragment : BaseFragment<FragmentContactDetailBinding, ContactDetailViewModel>() {

    override fun getViewBinding() = FragmentContactDetailBinding.inflate(layoutInflater)

    override val viewModelClass = ContactDetailViewModel::class.java

    override fun observeLiveData() {

    }

}