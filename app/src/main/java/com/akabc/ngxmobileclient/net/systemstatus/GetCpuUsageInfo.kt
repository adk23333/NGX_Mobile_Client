package com.akabc.ngxmobileclient.net.systemstatus

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.RequestKit
import org.json.JSONObject

class GetCpuUsageInfo(val url: String, val activity: Activity, val mainViewModel: MainViewModel) :
    BaseRequest() {
    override var tag: String = this.toString()
    override var body: JSONObject? = RequestKit().toJSONObject(
        "Offset" to 0,
        "Limit" to 1000,
        "Interval" to 0,
        "Percpu" to true
    )

    operator fun invoke() {
        super.request(url, activity, { response ->
            try {
                // Log.d(name, response.toString())
                val data = response.getJSONArray("Data")
                val cpUsage = mutableListOf<Double>()
                for (i in 0 until data.length()) {
                    Log.d(tag + object {}.javaClass.enclosingMethod?.name,
                        data.getDouble(i).toString())
                    cpUsage.add(data.getDouble(i))
                }
                mainViewModel.usageInfo(mainViewModel.usageInfo.value!!.copy(cpUsageInfo = cpUsage))
            } catch (e: Exception) {
                Log.w(tag + object {}.javaClass.enclosingMethod?.name, e.toString())
            }
        },
            { error ->
                Log.d(tag + object {}.javaClass.enclosingMethod?.name, error.toString())
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