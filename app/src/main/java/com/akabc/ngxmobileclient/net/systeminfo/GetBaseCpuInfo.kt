package com.akabc.ngxmobileclient.net.systeminfo

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest

class GetBaseCpuInfo(val url: String, val activity: Activity, val mainViewModel: MainViewModel) :
    BaseRequest() {
    override var tag: String = this.toString()

    operator fun invoke() {
        super.request(url, activity, { response ->
            try {
                Log.d(tag, response.toString())
                val data = response.getJSONArray("Data")
                mainViewModel.sysBaseInfo(mainViewModel.sysBaseInfo.value!!.copy(
                    coreNum = data.length(),
                    cpuName = data.getJSONObject(0).getString("modelName")))
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