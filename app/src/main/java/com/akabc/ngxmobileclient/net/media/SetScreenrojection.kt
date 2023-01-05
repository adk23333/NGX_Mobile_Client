package com.akabc.ngxmobileclient.net.media

import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.SingletonVolley
import org.json.JSONObject

class SetScreenProjection(
    val url: String,
    val path: String,
    renderId: String,
    val singletonVolley: SingletonVolley,
    val mainViewModel: MainViewModel,
) : BaseRequest() {
    override var tag: String = this.toString()

    override var body: JSONObject?=toJSONObject(
        "MediaSource" to "/$path",
        "RenderId" to renderId,
    )

    operator fun invoke() {
        super.request(url, singletonVolley, { response ->
            Log.d(tag, response.toString() + body.toString())

        },{ error ->
            Log.d(tag, error.toString())
        }){
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