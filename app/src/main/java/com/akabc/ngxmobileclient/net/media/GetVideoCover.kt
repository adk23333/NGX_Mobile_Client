package com.akabc.ngxmobileclient.net.media

import android.util.Log
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.SingletonVolley
import com.akabc.ngxmobileclient.ui.media.MediaViewModel

class GetVideoCover(
    val url: String,
    private val position: Int,
    val singletonVolley: SingletonVolley,
    private val mediaViewModel: MediaViewModel,
) : BaseRequest() {
    override var tag = this.toString()

    operator fun invoke() {
        super.request(url, singletonVolley, { imageContainer, _ ->
            imageContainer?.bitmap?.let {
                mediaViewModel.setVideoCover(it, position)
            }
        }, { error ->
            Log.d(tag, error.toString())
        })
    }

    init {
        invoke()
    }
}