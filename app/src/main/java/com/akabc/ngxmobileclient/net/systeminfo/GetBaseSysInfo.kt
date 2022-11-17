package com.akabc.ngxmobileclient.net.systeminfo

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.android.volley.VolleyError
import org.json.JSONObject

class GetBaseSysInfo : BaseRequest() {
    override var tag: String = this.toString()

    override fun onSuccess(activity: Activity, mainViewModel: MainViewModel, response: JSONObject) {
        try {
            Log.d(tag, response.toString())
            val data = response.getJSONObject("Data")
            mainViewModel.sysBaseInfo(mainViewModel.sysBaseInfo.value!!.copy(
                channel = data.getString("Channel"),
                proto = data.getString("Proto"),
                major = data.getString("Major"),
                minor = data.getString("Minor"),
                forBoard = data.getString("ForBoard"),
                buildInfo = data.getString("BuildInfo"),
                buildTime = data.getString("BuildTime")))
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