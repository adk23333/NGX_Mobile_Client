package com.akabc.ngxmobileclient.net.systemstatus

import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.SingletonVolley
import com.akabc.ngxmobileclient.ui.dashboard.DiskUsageInfo

class GetDiskUsageInfo(val url: String, val singletonVolley: SingletonVolley, val mainViewModel: MainViewModel): BaseRequest() {
    override var tag: String = this.toString()

    operator fun invoke() {
        super.request(url, singletonVolley, { response ->
            try {
                val data = response.getJSONArray("Data")
                val disks = mutableListOf<DiskUsageInfo>()
                for (i in 0 until data.length()) {
                    val disk = data.getJSONObject(i)
                    disks.add(DiskUsageInfo(disk.getLong("Used"), disk.getLong("Size")))
                }
                mainViewModel.usageInfo(mainViewModel.usageInfo.value!!.copy(diskUsageInfo = disks))
            } catch (e: Exception) {
                Log.w(tag + object{}.javaClass.enclosingMethod?.name, e.toString())
            }
        },
            { error ->
                Log.d(tag + object{}.javaClass.enclosingMethod?.name, error.toString())
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