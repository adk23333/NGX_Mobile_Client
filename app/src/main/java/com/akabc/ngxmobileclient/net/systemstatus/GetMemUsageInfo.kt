package com.akabc.ngxmobileclient.net.systemstatus

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.ui.dashboard.MemUsageInfo
import com.android.volley.VolleyError
import org.json.JSONObject

class GetMemUsageInfo: BaseRequest() {
    override var tag: String = this.toString()

    override fun onSuccess(activity: Activity, mainViewModel: MainViewModel, response: JSONObject) {
        try {
            Log.d(tag, response.toString())
            val data = response.getJSONObject("Data")
            val memPercent = data.getDouble("usedPercent")
            val memUsed = data.getLong("used")
            mainViewModel.usageInfo(mainViewModel.usageInfo.value!!.copy(memUsageInfo = MemUsageInfo(
                memPercent,
                memUsed)))
        } catch (e: Exception) {
            Log.w(tag + object{}.javaClass.enclosingMethod?.name, e.toString())
        }
    }

    override fun onError(error: VolleyError, mainViewModel: MainViewModel) {
        Log.d(tag + object{}.javaClass.enclosingMethod?.name, error.toString())
    }

    override fun headers(mainViewModel: MainViewModel): MutableMap<String, String>? {
        mainViewModel.loginResult.value?.success?.let {
            return mutableMapOf("Authorization" to it.token!!)
        }
        return null
    }

}