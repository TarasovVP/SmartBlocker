package com.tarasovvp.smartblocker.ui.main.settings.settings_privacy

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSettingsPrivacyBinding
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseBindingFragment

class SettingsPrivacyFragment :
    BaseBindingFragment<FragmentSettingsPrivacyBinding>() {

    override var layoutId = R.layout.fragment_settings_privacy

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            (activity as MainActivity).apply {
                settingsPrivacyWebView.webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                        setProgressVisibility(true)
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                        setProgressVisibility(false)
                    }
                }
            }
            settingsPrivacyWebView.loadData(getString(R.string.privacy_policy),
                "text/html; charset=utf-8",
                "UTF-8")
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).setProgressVisibility(false)
    }
}