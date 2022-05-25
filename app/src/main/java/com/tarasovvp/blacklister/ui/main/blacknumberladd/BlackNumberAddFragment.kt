package com.tarasovvp.blacklister.ui.main.blacknumberladd

import android.os.Bundle
import android.view.View
import com.tarasovvp.blacklister.databinding.FragmentBlackNumberAddBinding
import com.tarasovvp.blacklister.ui.base.BaseFragment

class BlackNumberAddFragment :
    BaseFragment<FragmentBlackNumberAddBinding, BlackNumberAddViewModel>() {

    override fun getViewBinding() = FragmentBlackNumberAddBinding.inflate(layoutInflater)

    override val viewModelClass = BlackNumberAddViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun observeLiveData() {

    }

}