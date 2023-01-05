package com.akabc.ngxmobileclient.net

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.R
import com.akabc.ngxmobileclient.data.User
import com.akabc.ngxmobileclient.data.Video
import com.akabc.ngxmobileclient.net.login.GetCaptcha
import com.akabc.ngxmobileclient.net.login.LoginRequest
import com.akabc.ngxmobileclient.net.media.*
import com.akabc.ngxmobileclient.net.systeminfo.GetBaseCpuInfo
import com.akabc.ngxmobileclient.net.systeminfo.GetBaseHardwareInfo
import com.akabc.ngxmobileclient.net.systeminfo.GetBaseSysInfo
import com.akabc.ngxmobileclient.net.systeminfo.GetMemInfo
import com.akabc.ngxmobileclient.net.systemstatus.GetCpuUsageInfo
import com.akabc.ngxmobileclient.net.systemstatus.GetDiskUsageInfo
import com.akabc.ngxmobileclient.net.systemstatus.GetMemUsageInfo
import com.akabc.ngxmobileclient.net.systemstatus.GetNetUsageInfo
import com.akabc.ngxmobileclient.ui.media.MediaViewModel
import com.android.volley.toolbox.ImageRequest
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.time.Instant


class Repository(val singletonVolley: SingletonVolley) {
    private val name = this.toString()

    // in-memory cache of the loggedInUser object
    var user = User()

    val isLoggedIn: Boolean
        get() = user.token != null


    fun checkLonginStatus(activity: Activity, mainViewModel: MainViewModel) {
        Thread {
            while (true) {
                user.token?.let {
                    val temp = user.exceptionTime - Instant.now().epochSecond
                    if (temp in 0..299 && user.captcha.ctId != null && user.captcha.ctCode != null) {
                        //TODO 刷新令牌
                        login(
                            user,
                            activity.getString(R.string.login_url),
                            mainViewModel,
                            true)
                    }
                    if (temp < 0 && user.captcha.ctId != null && user.captcha.ctCode != null) {
                        logout()
                    }
                }
                Thread.sleep(500)
            }
        }
    }

    fun logout() {
        user.token = null
        //login out
    }

    fun login(
        loginUser: User,
        url: String,
        mainViewModel: MainViewModel,
        isCheckLogin: Boolean,
    ) {
        // handle login
        LoginRequest(
            "http://${loginUser.ip}:${loginUser.port}${url}",
            loginUser, isCheckLogin,
            singletonVolley,
            mainViewModel)
    }

    fun getCaptcha(url: String, captchaImageUrl: String, mainViewModel: MainViewModel) {
        // val queue = SingletonVolley.getInstance(requireActivity().applicationContext).requestQueue
        GetCaptcha(
            "http://${user.ip}:${user.port}${url}",
            captchaImageUrl,
            singletonVolley,
            mainViewModel
        )
    }

    fun getCaptchaImage(
        captchaId: String,
        url: String,
        mainViewModel: MainViewModel,
    ) {
        val url2 = "http://${user.ip}:${user.port}${String.format(url, captchaId)}"
        val captchaImageRequest = ImageRequest(url2, { bitmap ->
            mainViewModel.setCaptcha(mainViewModel.captcha.value!!.copy(bitmap = bitmap))
        }, 240, 80, Bitmap.Config.RGB_565, { error ->
            Log.e(name, error.toString())
        })
        singletonVolley.addToRequestQueue(captchaImageRequest)
    }

    fun setLoggedInUser(loggedInUser: User) {
        this.user = loggedInUser
    }


    /**
     * 向服务器请求系统信息
     * **/
    fun getSysInfo(urls: List<String>, mainViewModel: MainViewModel) {
        getBaseSysInfo(urls[0], mainViewModel)
        getBaseHardwareInfo(urls[1], mainViewModel)
        getBaseCpuInfo(urls[2], mainViewModel)
        getMemInfo(urls[3], mainViewModel)
    }

    private fun getBaseSysInfo(url: String, mainViewModel: MainViewModel) {
        GetBaseSysInfo(
            "http://${user.ip}:${user.port}${url}",
            singletonVolley,
            mainViewModel)
    }

    private fun getBaseHardwareInfo(url: String, mainViewModel: MainViewModel) {
        GetBaseHardwareInfo(
            "http://${user.ip}:${user.port}${url}",
            singletonVolley,
            mainViewModel)
    }

    private fun getBaseCpuInfo(url: String, mainViewModel: MainViewModel) {
        GetBaseCpuInfo(
            "http://${user.ip}:${user.port}${url}",
            singletonVolley,
            mainViewModel)
    }

    private fun getMemInfo(url: String, mainViewModel: MainViewModel) {
        GetMemInfo(
            "http://${user.ip}:${user.port}${url}",
            singletonVolley,
            mainViewModel)
    }

    /**
     * 请求系统状态信息
     * **/
    fun getUsageInfo(urls: List<String>, mainViewModel: MainViewModel) {
        getCpuUsageInfo(urls[0], mainViewModel)
        getMemUsageInfo(urls[1], mainViewModel)
        getDiskUsageInfo(urls[2], mainViewModel)
        getNetUsageInfo(urls[3], mainViewModel)
    }

    private fun getCpuUsageInfo(url: String, mainViewModel: MainViewModel) {
        GetCpuUsageInfo(
            "http://${user.ip}:${user.port}${url}",
            singletonVolley,
            mainViewModel)
    }

    private fun getMemUsageInfo(url: String, mainViewModel: MainViewModel) {
        GetMemUsageInfo(
            "http://${user.ip}:${user.port}${url}",
            singletonVolley,
            mainViewModel)
    }

    private fun getDiskUsageInfo(url: String, mainViewModel: MainViewModel) {
        GetDiskUsageInfo(
            "http://${user.ip}:${user.port}${url}",
            singletonVolley,
            mainViewModel)
    }

    private fun getNetUsageInfo(url: String, mainViewModel: MainViewModel) {
        GetNetUsageInfo(
            "http://${user.ip}:${user.port}${url}",
            singletonVolley,
            mainViewModel)
    }

    /**
     * 获取媒体库
     * **/
    fun getVideos(
        url: String,
        mainViewModel: MainViewModel,
        mediaViewModel: MediaViewModel,
    ) {
        GetVideos(
            "http://${user.ip}:${user.port}${url}",
            singletonVolley,
            mainViewModel,
            mediaViewModel
        )

    }

    /** 获取封面图 **/
    fun getVideoThumbnail(url: String, position: Int, mediaViewModel: MediaViewModel) {
        GetVideoCover(
            "http://${user.ip}:${user.port}" + url + user.TID,
            position,
            singletonVolley,
            mediaViewModel
        )
    }

    /**
     * 获取视频地址及播放资源
     */
    fun getVideo(exoPlayer: ExoPlayer, video: Video, fragment: Fragment) {
        val mediaItem = MediaItem.fromUri(Uri.parse("http://${user.ip}:${user.port}${
            String.format(fragment.getString(R.string.video_download_url),
                video.Path, video.Name,
                true.toString(),
                user.TID)
        }"))
        exoPlayer.setMediaItem(mediaItem)
    }

    /**
     *  获取视频详情
     */
    fun getVideoDetails(
        path: String,
        fragment: Fragment,
        position: Int,
        mainViewModel: MainViewModel,
        mediaViewModel: MediaViewModel,
    ) {
        GetMediaDetails(
            "http://${user.ip}:${user.port}${fragment.getString(R.string.media_details_url)}",
            path,
            position,
            singletonVolley,
            mainViewModel,
            mediaViewModel
        )
    }

    /**
     *  获取DLNA投屏设备列表
     */

    fun getDlnaDevices(
        fragment: Fragment,
        mainViewModel: MainViewModel,
        mediaViewModel: MediaViewModel,
    ) {
        GetDlnaDevices(
            "http://${user.ip}:${user.port}${fragment.getString(R.string.media_find_dlna_url)}",
            singletonVolley,
            mainViewModel, mediaViewModel
        )
    }

    /**
     *  设置DLNA投屏设备
     */
    fun setScreenProjection(
        fragment: Fragment,
        path: String,
        renderId: String,
        mainViewModel: MainViewModel,
    ) {
        SetScreenProjection(
            "http://${user.ip}:${user.port}${fragment.getString(R.string.media_set_dlna_url)}",
            path, renderId, singletonVolley, mainViewModel
        )
    }

}








