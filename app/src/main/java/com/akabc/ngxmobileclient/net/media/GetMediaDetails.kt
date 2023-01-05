package com.akabc.ngxmobileclient.net.media

import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.data.VideoDetails
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.SingletonVolley
import com.akabc.ngxmobileclient.ui.media.MediaViewModel
import com.google.gson.Gson
import org.json.JSONObject

class GetMediaDetails(
    val url: String,
    val path: String,
    private val position: Int,
    val singletonVolley: SingletonVolley,
    val mainViewModel: MainViewModel,
    private val mediaViewModel: MediaViewModel,
) : BaseRequest() {
    override var tag = this.toString()

    override var body: JSONObject? = toJSONObject(
        "Path" to "/$path"
    )

    operator fun invoke() {

        super.request(url, singletonVolley, { response ->
            val data = response.getJSONObject("Data")

            val videoDetails = Gson().fromJson(data.toString(), VideoDetails::class.java)

            mediaViewModel.setVideoDetails(videoDetails, position)
        }, { error ->
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