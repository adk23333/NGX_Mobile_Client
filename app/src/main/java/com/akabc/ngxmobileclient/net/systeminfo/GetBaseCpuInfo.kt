package com.akabc.ngxmobileclient.net.systeminfo

import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.SingletonVolley

class GetBaseCpuInfo(val url: String, val singletonVolley: SingletonVolley, val mainViewModel: MainViewModel) :
    BaseRequest() {
    override var tag: String = this.toString()

    operator fun invoke() {
        super.request(url, singletonVolley, { response ->
            try {
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