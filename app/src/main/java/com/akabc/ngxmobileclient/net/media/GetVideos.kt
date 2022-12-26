package com.akabc.ngxmobileclient.net.media

import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.data.Video
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.SingletonVolley
import com.akabc.ngxmobileclient.ui.media.MediaViewModel
import org.json.JSONObject

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
        "Limit" to 50,
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
                val videos = mutableListOf<Video>()
                for (i in 0 until data.length()) {
                    val jsonVideo = data.getJSONObject(i)
                    val video = Video(
                        jsonVideo.getString("Id"),
                        jsonVideo.getString("Vid"),
                        jsonVideo.getInt("Uid"),
                        jsonVideo.getInt("Gid"),
                        jsonVideo.getLong("Size"),
                        jsonVideo.getString("Perm"),
                        jsonVideo.getLong("Atim"),
                        jsonVideo.getLong("Ctim"),
                        jsonVideo.getLong("Mtim"),
                        jsonVideo.getString("Type"),
                        jsonVideo.getString("Ext"),
                        jsonVideo.getString("Name"),
                        jsonVideo.getString("Path").replace("\\",""),
                    )
                    videos.add(video)
                }
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