package com.tarasovvp.blacklister.ui.main.numberdetail

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.databinding.FragmentNumberDetailBinding
import com.tarasovvp.blacklister.extensions.loadCircleImage
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.NumberInfo
import com.tarasovvp.blacklister.ui.base.BaseFragment

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
            })
            numberInfoLiveData.safeSingleObserve(viewLifecycleOwner, { numberInfo ->
                setNumberInfo(numberInfo)
            })
        }
    }

    private fun setContactInfo(contact: Contact) {
        binding?.numberDetailName?.text = contact.name
        binding?.numberDetailPhone?.text = contact.phone
        binding?.numberDetailAvatar?.loadCircleImage(contact.photoUrl)
    }

    private fun setNumberInfo(numberInfo: NumberInfo) {
        binding?.numberDetailRatingsTitle?.text = numberInfo.ratings.ratingsTitle
        numberInfo.ratings.ratingsList.forEach { ratingsTitle ->
            val ratingsTextView = TextView(context)
            ratingsTextView.text = ratingsTitle
            binding?.numberDetailRatingsList?.addView(ratingsTextView)
        }
        binding?.numberDetailCategoriesTitle?.text = numberInfo.categories.categoriesTitle
        numberInfo.categories.categoriesList.forEach { categoriesTitle ->
            val categoriesTextView = TextView(context)
            categoriesTextView.text = categoriesTitle
            binding?.numberDetailCategoriesList?.addView(categoriesTextView)
        }
    }

}