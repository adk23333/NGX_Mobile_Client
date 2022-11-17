package com.akabc.ngxmobileclient.net.systeminfo

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.android.volley.VolleyError
import org.json.JSONObject

class GetMemInfo: BaseRequest() {
    override var tag: String = this.toString()

    override fun onSuccess(activity: Activity, mainViewModel: MainViewModel, response: JSONObject) {
        try {
            Log.d(tag, response.toString())
            val data = response.getJSONObject("Data")
            val mem = data.getJSONArray("children")
            val memLen = mem.length()
            var i = 0
            var memSumSize: Long = 0
            while (i < memLen) {
                val m = mem.getJSONObject(i)
                memSumSize += m.getLong("size")
                i++
            }
            mainViewModel.sysBaseInfo(mainViewModel.sysBaseInfo.value!!.copy(memSumSize = memSumSize))
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