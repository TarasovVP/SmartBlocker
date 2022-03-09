package com.example.blacklister.ui.contactdetail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.navArgs
import com.example.blacklister.databinding.FragmentContactDetailBinding
import com.example.blacklister.extensions.loadCircleImage
import com.example.blacklister.ui.base.BaseFragment
import com.google.gson.Gson

class ContactDetailFragment : BaseFragment<FragmentContactDetailBinding, ContactDetailViewModel>() {

    override fun getViewBinding() = FragmentContactDetailBinding.inflate(layoutInflater)

    override val viewModelClass = ContactDetailViewModel::class.java

    private val args: ContactDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.contactDetailName?.text = args.contact?.name
        binding?.contactDetailPhone?.text = args.contact?.phone
        binding?.contactDetailBlackListNumber?.isChecked = args.contact?.isBlackList == true
        binding?.contactDetailAvatar?.loadCircleImage(args.contact?.photoUrl)
        binding?.contactDetailBlackListNumber?.setOnCheckedChangeListener { _, isChecked ->
            args.contact?.isBlackList = isChecked
            args.contact?.let { viewModel.updateContact(it) }
        }
    }

    override fun observeLiveData() {

    }

}