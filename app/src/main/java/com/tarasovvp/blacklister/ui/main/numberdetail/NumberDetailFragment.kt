package com.tarasovvp.blacklister.ui.main.numberdetail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentNumberDetailBinding
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.loadCircleImage
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.NumberInfo
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.fragment_number_detail.*

class NumberDetailFragment : BaseFragment<FragmentNumberDetailBinding, NumberDetailViewModel>() {

    override fun getViewBinding() = FragmentNumberDetailBinding.inflate(layoutInflater)

    override val viewModelClass = NumberDetailViewModel::class.java

    private val args: NumberDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.number?.let { viewModel.getContact(it) }
    }

    override fun observeLiveData() {
        with(viewModel) {
            numberDetailLiveData.safeSingleObserve(viewLifecycleOwner, { contact ->
                setContactInfo(contact)
                if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
                    getNumberInfo(contact.phone.toString())
                } else {
                    binding?.includeNoAccount?.root?.isVisible = true
                    binding?.includeNoAccount?.noAccountIcon?.isVisible = false
                    binding?.includeNoAccount?.noAccountBtn?.setSafeOnClickListener {
                        findNavController().navigate(NumberDetailFragmentDirections.startLoginFragment())
                    }
                    binding?.numberDetailType?.setImageResource(when {
                        contact.isBlackList -> R.drawable.ic_block
                        contact.isWhiteList -> R.drawable.ic_accepted
                        else -> 0
                    })
                }
            })
            blackNumberAmountLiveData.safeSingleObserve(viewLifecycleOwner, {
                Log.e("detailTAG", "NumberDetailFragment blackNumberList ${Gson().toJson(it)}")
                binding?.numberDetailRatingsTitle?.text = String.format("%s %s", "Количество пользователей, которые заблокировали этот номер - ", it.size)
                binding?.numberDetailCategoriesTitle?.text = String.format("%s %s", "В таких категориях:", it.map { blackNumber ->
                    blackNumber?.category?.let { it1 ->

                } }.joinToString(", "))
            })
        }
    }

    private fun setContactInfo(contact: Contact) {
        binding?.numberDetailName?.text = contact.name
        binding?.numberDetailPhone?.text = contact.phone
        binding?.numberDetailAvatar?.loadCircleImage(contact.photoUrl)
    }
}