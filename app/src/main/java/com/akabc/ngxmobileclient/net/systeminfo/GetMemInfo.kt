package com.akabc.ngxmobileclient.net.systeminfo

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest

class GetMemInfo(val url: String, val activity: Activity, val mainViewModel: MainViewModel): BaseRequest() {
    override var tag: String = this.toString()

    operator fun invoke() {
        super.request(url, activity, { response ->
            try {
                Log.d(tag, response.toString())
                val data = response.getJSONObject("Data")
                val mem = data.getJSONArray("children")
                val memLen = mem.length()
                var i = 0
                var memSumSize: Long = 0
                while (i < memLen) {
                    val m = mem.getJSONObject(i)
                    memSumSize += m.getLong("size")
                    i++
                }
                mainViewModel.sysBaseInfo(mainViewModel.sysBaseInfo.value!!.copy(memSumSize = memSumSize))
            } catch (e: Exception) {
                Log.w(tag, e.toString())
            }
        },
            { error ->
                Log.d(tag, error.toString())
            }
        ) {
            mainViewModel.loginResult.value?.success?.let {
                return@request mutableMapOf("Authorization" to it.token!!)
            }
            null
        }
    }

    init {
        invoke()
    }
}