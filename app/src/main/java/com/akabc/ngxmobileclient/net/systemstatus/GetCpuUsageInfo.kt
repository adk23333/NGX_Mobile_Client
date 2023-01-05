package com.akabc.ngxmobileclient.net.systemstatus

import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.SingletonVolley
import org.json.JSONObject

class GetCpuUsageInfo(val url: String, val singletonVolley: SingletonVolley, val mainViewModel: MainViewModel) :
    BaseRequest() {
    override var tag: String = this.toString()
    override var body: JSONObject? = toJSONObject(
        "Offset" to 0,
        "Limit" to 1000,
        "Interval" to 0,
        "Percpu" to true
    )

    operator fun invoke() {
        super.request(url, singletonVolley, { response ->
            try {
                val data = response.getJSONArray("Data")
                val cpUsage = mutableListOf<Double>()
                for (i in 0 until data.length()) {
                    cpUsage.add(data.getDouble(i))
                }
                mainViewModel.usageInfo(mainViewModel.usageInfo.value!!.copy(cpUsageInfo = cpUsage))
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