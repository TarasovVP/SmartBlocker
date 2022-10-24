package com.tarasovvp.blacklister.ui.main.call_detail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentCallDetailBinding
import com.tarasovvp.blacklister.ui.base.BaseFragment

class CallDetailFragment : BaseFragment<FragmentCallDetailBinding, CallDetailViewModel>() {

    override var layoutId = R.layout.fragment_call_detail
    override val viewModelClass = CallDetailViewModel::class.java

    private val args: CallDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.call = args.call
    }

    override fun observeLiveData() {

    }
}