package com.dm.checker

import android.annotation.SuppressLint
import android.view.View
import android.webkit.WebView

object WebSettingsUtils {

    const val MOBILE =
        "Mozilla/5.0 (Linux; U; Android 4.4; en-us; Nexus 4 Build/JOP24G) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"

    const val DESK =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36"

    const val SKYPE =
        "@Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36"

    fun webSettings(webView: WebView) {
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.overScrollMode = View.OVER_SCROLL_NEVER
        webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        webView.isScrollbarFadingEnabled = false
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun webMobile(webView: WebView) {
        webView.settings.allowContentAccess = true
        webView.settings.setAppCacheEnabled(true)
        webView.settings.loadWithOverviewMode = true
        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.saveFormData = true
        webView.settings.domStorageEnabled = true
    }

}
