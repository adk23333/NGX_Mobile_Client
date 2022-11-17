package com.akabc.ngxmobileclient.net.systemstatus

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.RequestKit
import com.android.volley.VolleyError
import org.json.JSONObject

class CpuUsageInfo : BaseRequest() {
    override var tag: String = this.toString()
    override var body = RequestKit().toJSONObject(
        "Offset" to 0,
        "Limit" to 1000,
        "Interval" to 0,
        "Percpu" to true
    )

    override fun onSuccess(activity: Activity, mainViewModel: MainViewModel, response: JSONObject) {
        try {
            // Log.d(name, response.toString())
            val data = response.getJSONArray("Data")
            val cpUsage = mutableListOf<Double>()
            for (i in 0 until data.length()) {
                Log.d(tag, data.getDouble(i).toString())
                cpUsage.add(data.getDouble(i))
            }
            mainViewModel.usageInfo(mainViewModel.usageInfo.value!!.copy(cpUsageInfo = cpUsage))
        } catch (e: Exception) {
            Log.w(tag, e.toString())
        }
    }

    override fun onError(error: VolleyError) {
        Log.d(tag, error.toString())
    }

    override fun headers(mainViewModel: MainViewModel): MutableMap<String, String>? {
        mainViewModel.loginResult.value?.success?.let {
            return mutableMapOf("Authorization" to it.token!!)
        }
        return null
    }
}