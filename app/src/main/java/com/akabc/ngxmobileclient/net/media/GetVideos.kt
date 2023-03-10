package com.akabc.ngxmobileclient.net.media

import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.data.Video
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.SingletonVolley
import com.akabc.ngxmobileclient.ui.media.MediaViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

const val LIMIT = 100

class GetVideos(
    val url: String,
    val singletonVolley: SingletonVolley,
    val mainViewModel: MainViewModel,
    val mediaViewModel: MediaViewModel,
) : BaseRequest() {
    override var tag: String = this.toString()

    override var body: JSONObject? = toJSONObject(
        "Count" to false,
        "Name" to "",
        "Exts" to setOf("MP4", "MOV", "WMV", "MKV", "FLV", "M4V", "AVI"),
        "Limit" to LIMIT,
        "Order" to "Mtim",
        "Asc" to false,
        "MtimBegin" to 0,
        "LastDay" to -1,
        "Path" to ""
    )

    operator fun invoke() {
        super.request(url, singletonVolley, { response ->
            try {
                val data = response.getJSONArray("Data")
                val videos: List<Video> = Gson().fromJson(data.toString(), object : TypeToken<List<Video>>(){}.type)

                mediaViewModel.setVideos(videos)
            } catch (e: Exception) {
                Log.w(tag, e.toString())
            }
        }, { error ->
            Log.d(tag, error.toString())
        })
        {
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