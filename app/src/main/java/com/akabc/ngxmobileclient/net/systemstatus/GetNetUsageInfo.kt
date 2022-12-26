package com.akabc.ngxmobileclient.net.systemstatus

import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.SingletonVolley
import com.akabc.ngxmobileclient.ui.dashboard.NetUsageInfo
import org.json.JSONObject

class GetNetUsageInfo(val url: String, val singletonVolley: SingletonVolley, val mainViewModel: MainViewModel) :
    BaseRequest() {
    override var tag: String = this.toString()
    override var body: JSONObject? = toJSONObject(
        "Offset" to 0,
        "Limit" to 1000,
        "Pernic" to true
    )

    operator fun invoke() {
        super.request(url, singletonVolley, { response ->
            try {
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