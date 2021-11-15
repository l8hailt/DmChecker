package com.dm.checker

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import com.dm.checker.databinding.ActivityMainBinding
import org.apache.commons.net.whois.WhoisClient
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName

    private lateinit var binding: ActivityMainBinding

    private lateinit var preferences: SharedPreferences
    private lateinit var whoisClient: WhoisClient

    private lateinit var progressDialog: ProgressDialog

    private var isLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = getPreferences(Context.MODE_PRIVATE)
        whoisClient = WhoisClient()

        progressDialog = ProgressDialog(this).apply {
            setMessage("Processing...")
            setCancelable(false)
        }

        binding.btnSearch.setOnClickListener {
            if (isLoggedIn) {
                doCheckDomain()
            } else {
                doLogin()
            }
        }

        binding.btnBuy.setOnClickListener {
            progressDialog.show()
        }
    }

    private fun doCheckDomain() {
        hideKeyboard(this)
        progressDialog.show()

        val domain = binding.edtInput.text.toString().trim()
        Thread {
            try {
                whoisClient.connect(WhoisClient.DEFAULT_HOST)
                whoisClient.query(domain).also { result ->
                    Log.e(TAG, "doCheckDomain: $result")
                    if (result.lowercase().contains("no match for")) {
                        val subResult = result.substring(0, result.indexOf(">>>"))
                        Handler(mainLooper).postDelayed({
                            progressDialog.dismiss()
                            binding.tvResult.text = subResult
                            binding.btnBuy.visibility = View.VISIBLE
                        }, 2000)
                    } else {
                        val subResult = result.substring(0, result.indexOf(">>>"))
                        Handler(mainLooper).postDelayed({
                            progressDialog.dismiss()
                            binding.tvResult.text = subResult
                            binding.btnBuy.visibility = View.GONE
                        }, 2000)
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "doCheckDomain: ${e.message}")
            } finally {
                whoisClient.disconnect()
            }
        }.start()
    }

    private fun doLogin() {
        Intent(this, LoginActivity::class.java).also { loginIntent ->
            resultLauncher.launch(loginIntent)
        }
    }

    private fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                Log.e(TAG, "result: OK")
                val data: Intent? = result.data
                isLoggedIn = true
                binding.btnSearch.text = "Check >"
            }
        }

}