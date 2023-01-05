package com.akabc.ngxmobileclient.net.media

import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.data.DlnaDevice
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.SingletonVolley
import com.akabc.ngxmobileclient.net.forEach
import com.akabc.ngxmobileclient.ui.media.MediaViewModel

class GetDlnaDevices(
    val url: String,
    val singletonVolley: SingletonVolley,
    val mainViewModel: MainViewModel,
    val mediaViewModel: MediaViewModel,
) : BaseRequest() {
    override var tag: String = this.toString()

    operator fun invoke() {
        super.request(url, singletonVolley, { response ->
            try {
                val data = response.getJSONArray("Data")
                val devices = mutableListOf<DlnaDevice>()
                data.forEach {
                    devices.add(DlnaDevice(
                        it.getString("Type"),
                        it.getString("USN"),
                        it.getString("Location"),
                        it.getString("Server"),
                        it.getString("Id"),
                        it.getString("Name"),
                        it.getString("Host"),
                    ))
                }
                mediaViewModel.setDlnaDevices(devices)
            } catch (e: Exception) {
                Log.w(tag, e.toString())
            }
        }, { error ->
            Log.d(tag, error.toString())
        }) {
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