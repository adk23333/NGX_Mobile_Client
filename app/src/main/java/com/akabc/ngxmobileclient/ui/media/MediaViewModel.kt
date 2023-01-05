package com.akabc.ngxmobileclient.ui.media

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akabc.ngxmobileclient.data.DlnaDevice
import com.akabc.ngxmobileclient.data.Video
import com.akabc.ngxmobileclient.data.VideoDetails
import com.akabc.ngxmobileclient.net.Repository

class MediaViewModel(): ViewModel() {

    private var _videos = MutableLiveData<List<Video>>().apply {
        //value = "This is gallery Fragment"
    }
    val videos: LiveData<List<Video>> = _videos

    fun setVideos(videos: List<Video>){
        _videos.value = videos
    }

    fun setVideoCover(bitmap: Bitmap, position: Int) {
        _videos.value = videos.value?.toMutableList().apply {
            this!![position] = this[position].copy(cover = bitmap)
        }
    }

    fun setVideoDetails(details: VideoDetails, position: Int){
        _videos.value = videos.value?.toMutableList().apply {
            this!![position] = this[position].copy(details = details)
        }
    }

    private var _dlnaDevices = MutableLiveData<List<DlnaDevice>>()
    val dlnaDevices: LiveData<List<DlnaDevice>> = _dlnaDevices

    fun setDlnaDevices(devices: List<DlnaDevice>){
        _dlnaDevices.value = devices
    }

    var currentDevice: DlnaDevice? = null
}