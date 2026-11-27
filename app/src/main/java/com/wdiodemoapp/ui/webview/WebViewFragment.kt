package com.wdiodemoapp.ui.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.wdiodemoapp.IdlingHooks
import com.wdiodemoapp.databinding.FragmentWebviewBinding

class WebViewFragment : Fragment() {

    private var _binding: FragmentWebviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WebView.setWebContentsDebuggingEnabled(true)

        binding.webview.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?,
                ): Boolean = false

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    _binding?.webviewProgress?.visibility = View.VISIBLE
                    IdlingHooks.onWebViewLoading(true)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    _binding?.webviewProgress?.visibility = View.GONE
                    IdlingHooks.onWebViewLoading(false)
                }
            }

            if (url == null) {
                loadUrl(WEBDRIVER_IO_URL)
            }
        }
    }

    override fun onDestroyView() {
        IdlingHooks.onWebViewLoading(false)
        binding.webview.destroy()
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val WEBDRIVER_IO_URL = "https://webdriver.io/"
    }
}
