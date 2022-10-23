package com.tarasovvp.blacklister.ui.main.filter_detail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentFilterDetailBinding
import com.tarasovvp.blacklister.extensions.isNull
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.main.filter_add.ContactFilterAdapter

class FilterDetailFragment : BaseFragment<FragmentFilterDetailBinding, FilterDetailViewModel>() {

    override var layoutId = R.layout.fragment_filter_detail
    override val viewModelClass = FilterDetailViewModel::class.java
    private val args: FilterDetailFragmentArgs by navArgs()

    private var contactFilterAdapter: ContactFilterAdapter? = null
    private var contactFilterList: ArrayList<BaseAdapter.MainData> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.filter?.apply {
            binding?.filter = this
            viewModel.getQueryContactList(this)
        }
        setContactAdapter()
    }

    private fun setContactAdapter() {
        if (contactFilterAdapter.isNull()) {
            contactFilterAdapter = ContactFilterAdapter(contactFilterList) { phone ->

            }
            binding?.filterDetailFilterList?.adapter = contactFilterAdapter
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            queryContactListLiveData.safeSingleObserve(viewLifecycleOwner) { contactFilterList ->
                contactFilterAdapter?.contactFilterList = contactFilterList
                contactFilterAdapter?.notifyDataSetChanged()
            }
        }
    }
}