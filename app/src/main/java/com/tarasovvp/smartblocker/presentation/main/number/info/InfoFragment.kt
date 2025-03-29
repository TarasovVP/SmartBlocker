package com.tarasovvp.smartblocker.presentation.main.number.info

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentInfoBinding
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.presentation.base.BaseBindingFragment
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.utils.TextViewLinkHandler
import com.tarasovvp.smartblocker.utils.extensions.htmlWithImages

class InfoFragment :
    BaseBindingFragment<FragmentInfoBinding>() {
    override var layoutId = R.layout.fragment_info
    private val args: InfoFragmentArgs by navArgs()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.toolbar?.title = getString(args.info.title())
        binding?.infoWebView?.apply {
            text = context.htmlWithImages(getString(args.info.description()))
            movementMethod =
                object : TextViewLinkHandler() {
                    override fun onLinkClick(url: String?) {
                        url?.let { Info.valueOf(it) }?.apply {
                            findNavController().navigate(InfoFragmentDirections.startInfoFragment(info = this))
                        }
                    }
                }
        }
    }
}
