package com.akabc.ngxmobileclient.net.systeminfo

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest

class GetBaseSysInfo(val url: String, val activity: Activity, val mainViewModel: MainViewModel) : BaseRequest() {
    override var tag: String = this.toString()

    operator fun invoke() {
        super.request(url, activity, { response ->
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