package com.akabc.ngxmobileclient.net.systeminfo

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.android.volley.VolleyError
import org.json.JSONObject

class GetBaseCpuInfo : BaseRequest() {
    override var tag: String = this.toString()

    fun get(activity: Activity, mainViewModel: MainViewModel) {
        super.get(activity, mainViewModel,
            { response ->
                try {
                    Log.d(tag, response.toString())
                    val data = response.getJSONArray("Data")
                    mainViewModel.sysBaseInfo(mainViewModel.sysBaseInfo.value!!.copy(
                        coreNum = data.length(),
                        cpuName = data.getJSONObject(0).getString("modelName")))
                } catch (e: Exception) {
                    Log.w(tag, e.toString())
                }
            },
            { error ->
                Log.d(tag, error.toString())
            },
            {
                mainViewModel.loginResult.value?.success?.let {
                    return@let mutableMapOf("Authorization" to it.token!!)
                }
                null
            }
        )
    }

//    override fun onSuccess(activity: Activity, mainViewModel: MainViewModel, response: JSONObject) {
//        try {
//            Log.d(tag, response.toString())
//            val data = response.getJSONArray("Data")
//            mainViewModel.sysBaseInfo(mainViewModel.sysBaseInfo.value!!.copy(
//                coreNum = data.length(),
//                cpuName = data.getJSONObject(0).getString("modelName")))
//        } catch (e: Exception) {
//            Log.w(tag, e.toString())
//        }
//    }
//
//    override fun onError(error: VolleyError, mainViewModel: MainViewModel) {
//        Log.d(tag, error.toString())
//    }
//
//    override fun headers(mainViewModel: MainViewModel): MutableMap<String, String>? {
//        mainViewModel.loginResult.value?.success?.let {
//            return mutableMapOf("Authorization" to it.token!!)
//        }
//        return null
//    }
}