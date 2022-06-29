package com.tarasovvp.blacklister.ui.main.numberdetail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentNumberDetailBinding
import com.tarasovvp.blacklister.enum.BlackNumberCategory
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.loadCircleImage
import com.tarasovvp.blacklister.extensions.orZero
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

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
                }
            })
            blackNumberAmountLiveData.safeSingleObserve(viewLifecycleOwner, {
                binding?.numberDetailRatingsTitle?.text = String.format("%s %s",
                    "Количество пользователей, которые заблокировали этот номер - ",
                    it.size)
                val categoriesList = it.map { blackNumber ->
                    blackNumber?.category?.let { id ->
                        getString(BlackNumberCategory.findBlackNumberCategoryById(id)?.title.orZero())
                    }
                }.joinToString(", ")
                if (categoriesList.isNotEmpty()) binding?.numberDetailCategoriesTitle?.text =
                    String.format("%s %s", "В таких категориях:", categoriesList)
            })
        }
    }

    private fun setContactInfo(contact: Contact) {
        binding?.numberDetailName?.text = contact.name
        binding?.numberDetailPhone?.text = contact.phone
        binding?.numberDetailAvatar?.loadCircleImage(contact.photoUrl)
        binding?.numberDetailType?.setImageResource(when {
            contact.isBlackList -> R.drawable.ic_block
            contact.isWhiteList -> R.drawable.ic_accepted
            else -> 0
        })
    }
}