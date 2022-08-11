package com.tarasovvp.blacklister.ui.main.contact_detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.ADD_TO_LIST
import com.tarasovvp.blacklister.constants.Constants.PLUS_CHAR
import com.tarasovvp.blacklister.constants.Constants.WHITE_LIST
import com.tarasovvp.blacklister.databinding.FragmentContactDetailBinding
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.loadCircleImage
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class ContactDetailFragment : BaseFragment<FragmentContactDetailBinding, ContactDetailViewModel>() {

    override fun getViewBinding() = FragmentContactDetailBinding.inflate(layoutInflater)

    override val viewModelClass = ContactDetailViewModel::class.java

    private val args: ContactDetailFragmentArgs by navArgs()

    private var expandableNumberAdapter: ContactDetailAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPriority()
        args.phone?.let { phone ->
            if (phone.isEmpty()) {
                viewModel.contactDetailLiveData.postValue(Contact(name = getString(R.string.hidden)))
            } else {
                viewModel.getContact(phone.filter { it.isDigit() || it == PLUS_CHAR })
            }
            viewModel.getBlackFilterList(phone.filter { it.isDigit() || it == PLUS_CHAR })
            viewModel.getWhiteFilterList(phone.filter { it.isDigit() || it == PLUS_CHAR })
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            contactDetailLiveData.safeSingleObserve(viewLifecycleOwner) { contact ->
                setContactInfo(contact)
            }
            blackFilterLiveData.safeSingleObserve(viewLifecycleOwner) { blackNumberList ->
                setNumberList(blackNumberList, true)
            }
            whiteFilterLiveData.safeSingleObserve(viewLifecycleOwner) { whiteNumberList ->
                setNumberList(whiteNumberList, false)
            }
        }
    }

    private fun setPriority() {
        binding?.filterDetailPriority?.setCompoundDrawablesWithIntrinsicBounds(0,
            0,
            if (SharedPreferencesUtil.isWhiteListPriority) R.drawable.ic_white_filter else R.drawable.ic_black_filter,
            0)
        binding?.filterDetailPriority?.setSafeOnClickListener {
            findNavController().navigate(ContactDetailFragmentDirections.startBlockSettingsFragment())
        }
    }

    private fun setContactInfo(contact: Contact) {
        binding?.apply {
            filterDetailName.text = contact.name
            filterDetailPhone.text = contact.phone
            filterDetailAvatar.loadCircleImage(contact.photoUrl)
            filterDetailType.setImageResource(when {
                contact.isBlackFilter -> R.drawable.ic_block
                contact.isWhiteFilter -> R.drawable.ic_accepted
                else -> 0
            })
            filterDetailPriority.text = String.format(getString(R.string.prioritness),
                if (SharedPreferencesUtil.isWhiteListPriority) getString(R.string.white_list) else getString(
                    R.string.black_list))
            filterDetailAddFilter.setSafeOnClickListener {
                findNavController().navigate(ContactDetailFragmentDirections.startAddToListDialog())
            }
        }
        setFragmentResultListener(ADD_TO_LIST) { _, bundle ->
            findNavController().navigate(ContactDetailFragmentDirections.startFilterAddFragment(
                filter = Filter(filter = contact.trimmedPhone).apply {
                    isBlackFilter = bundle.getBoolean(WHITE_LIST).not()
                }))
        }
    }

    private fun setNumberList(filterList: List<Filter>, isBlackList: Boolean) {
        val title =
            "Найдено ${filterList.size} фильтров из ${if (isBlackList) "черного списка" else "белого списка"}"
        if (expandableNumberAdapter.isNotNull()) {
            expandableNumberAdapter?.titleList?.add(title)
            expandableNumberAdapter?.filterListMap?.put(title, filterList)
        } else {
            expandableNumberAdapter =
                ContactDetailAdapter(arrayListOf(title), hashMapOf(title to filterList))
            binding?.filterDetailFilterList?.setAdapter(expandableNumberAdapter)
            binding?.filterDetailFilterList?.setOnChildClickListener { _, _, _, childPosition, _ ->
                findNavController().navigate(ContactDetailFragmentDirections.startFilterAddFragment(
                    filter = filterList[childPosition]))
                return@setOnChildClickListener true
            }
        }
    }
}