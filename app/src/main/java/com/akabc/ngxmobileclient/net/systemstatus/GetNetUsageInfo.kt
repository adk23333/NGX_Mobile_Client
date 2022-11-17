package com.akabc.ngxmobileclient.net.systemstatus

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.RequestKit
import com.akabc.ngxmobileclient.ui.dashboard.NetUsageInfo
import com.android.volley.VolleyError
import org.json.JSONObject

class GetNetUsageInfo:BaseRequest() {
    override var tag: String = this.toString()
    override var body: JSONObject? = RequestKit().toJSONObject(
        "Offset" to 0,
        "Limit" to 1000,
        "Pernic" to true
    )

    override fun onSuccess(activity: Activity, mainViewModel: MainViewModel, response: JSONObject) {
        try {
            Log.d(tag, response.toString())
            val data = response.getJSONArray("Data")
            val time = response.getLong("Time")
            val nets = mutableListOf<NetUsageInfo>()
            for (i in 0 until data.length()) {
                val net = data.getJSONObject(i)
                val sent = net.getLong("bytesSent")
                val recv = net.getLong("bytesRecv")
                if (mainViewModel.usageInfo.value!!.netUsageInfo.size != data.length()) {
                    nets.add(NetUsageInfo(sent, recv, 1, 1, 1, 1))
                } else {
                    nets.add(NetUsageInfo(sent, recv,
                        sent - mainViewModel.usageInfo.value!!.netUsageInfo[i].bytesSent,
                        recv - mainViewModel.usageInfo.value!!.netUsageInfo[i].bytesRecv,
                        time,
                        time - mainViewModel.usageInfo.value!!.netUsageInfo[i].time))
                }
            }
            mainViewModel.usageInfo(mainViewModel.usageInfo.value!!.copy(netUsageInfo = nets))
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