package com.akabc.ngxmobileclient.ui.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akabc.ngxmobileclient.data.Video

class MediaViewModel: ViewModel() {

    private var _videos = MutableLiveData<List<Video>>().apply {
        //value = "This is gallery Fragment"
    }
    val videos: LiveData<List<Video>> = _videos

    fun setVideos(videos: List<Video>){
        _videos.value = videos
    }
}