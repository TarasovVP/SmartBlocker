package com.tarasovvp.smartblocker.ui.main.number.info

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentInfoBinding
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseBindingFragment

class InfoFragment :
    BaseBindingFragment<FragmentInfoBinding>() {

    override var layoutId = R.layout.fragment_info

    private val args: InfoFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).apply {
            toolbar?.title = args.info?.title
            binding?.infoWebView?.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    setProgressVisibility(true)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    setProgressVisibility(false)
                }
            }
        }
        args.info?.description?.let {
            binding?.infoWebView?.loadDataWithBaseURL("file:///android_res/drawable/", it,
                "text/html; charset=utf-8",
                "UTF-8", null)
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).setProgressVisibility(false)
    }
}