package com.dm.checker

import android.os.Bundle
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.dm.checker.databinding.ActivityLookupBinding
import org.jsoup.Jsoup
import android.webkit.ConsoleMessage

import android.webkit.WebChromeClient





class LookupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLookupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLookupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSearch.setOnClickListener {
//            Thread {
//                val document = Jsoup.connect("https://whois.inet.vn/whois?domain=google.com").get()
//                val elements = document.select("div#domainAvailabilityPowerBarHeading")
//                Log.e("TAG", "onCreate: $document")
//                Log.e("TAG", "onCreate: $elements")
//            }.start()
//            val script = "\$('.search-button').click()"
            val script = "javascript:(function(){" +
                    "var l = document.getElementsByClassName('search-button')[0];" +
                    "var r = l.click();})()"
//            val script = "javascript:(function(){" +
//                    "var l = document.getElementsByClassName('input_inner')[0];" +
//                    "return l.value = \'google.com\';})()"
            binding.wvSearch.evaluateJavascript(script) { result ->
                Log.e("TAG", "onCreate: $result")
            }
        }

        binding.wvSearch.apply {
            WebSettingsUtils.webSettings(this)
            WebSettingsUtils.webMobile(this)
            settings.userAgentString = WebSettingsUtils.DESK
        }
        binding.wvSearch.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.e("TAG", "onPageFinished: ")
            }
        }

        binding.wvSearch.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d("TAG", "onConsoleMessage: ${consoleMessage.message()}")
                return true
            }
        }

        binding.wvSearch.loadUrl("https://whois.ffis.me/")

    }
}