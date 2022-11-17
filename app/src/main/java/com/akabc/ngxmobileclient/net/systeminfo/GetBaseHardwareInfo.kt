package com.akabc.ngxmobileclient.net.systeminfo

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.android.volley.VolleyError
import org.json.JSONObject

class GetBaseHardwareInfo : BaseRequest() {
    override var tag: String = this.toString()

    override fun onSuccess(activity: Activity, mainViewModel: MainViewModel, response: JSONObject) {
        try {
            Log.d(tag, response.toString())
            val data = response.getJSONObject("Data")
            val info = data.getJSONObject("info")
            mainViewModel.sysBaseInfo(mainViewModel.sysBaseInfo.value!!.copy(
                platform = info.getString("platform"),
                platformVersion = info.getString("platformVersion"),
                kernelArch = info.getString("kernelArch")))
        } catch (e: Exception) {
            Log.w(tag, e.toString())
        }
    }

    override fun onError(error: VolleyError, mainViewModel: MainViewModel) {
        Log.d(tag, error.toString())
    }

    override fun headers(mainViewModel: MainViewModel): MutableMap<String, String>? {
        mainViewModel.loginResult.value?.success?.let {
            return mutableMapOf("Authorization" to it.token!!)
        }
        return null
    }
}