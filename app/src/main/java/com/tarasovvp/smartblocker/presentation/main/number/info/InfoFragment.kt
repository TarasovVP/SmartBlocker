package com.tarasovvp.smartblocker.presentation.main.number.info

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentInfoBinding
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.utils.extensions.htmlWithImages
import com.tarasovvp.smartblocker.domain.entities.models.InfoData
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.base.BaseBindingFragment
import com.tarasovvp.smartblocker.utils.TextViewLinkHandler

class InfoFragment :
    BaseBindingFragment<FragmentInfoBinding>() {

    override var layoutId = R.layout.fragment_info
    private val args: InfoFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).toolbar?.title = args.info?.title
        binding?.infoWebView?.apply {
            text = args.info?.description?.let { context?.htmlWithImages(it) }
            movementMethod = object : TextViewLinkHandler() {
                override fun onLinkClick(url: String?) {
                    url?.let { Info.valueOf(it) }?.apply {
                        findNavController().navigate(
                            InfoFragmentDirections.startInfoFragment(
                                info = InfoData(
                                    title = getString(title()),
                                    description = getString(description())
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}