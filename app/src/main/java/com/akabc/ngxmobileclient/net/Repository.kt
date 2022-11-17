package com.akabc.ngxmobileclient.net

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.R
import com.akabc.ngxmobileclient.net.login.GetCaptcha
import com.akabc.ngxmobileclient.net.login.LoginRequest
import com.akabc.ngxmobileclient.net.systeminfo.GetBaseCpuInfo
import com.akabc.ngxmobileclient.net.systeminfo.GetBaseHardwareInfo
import com.akabc.ngxmobileclient.net.systeminfo.GetBaseSysInfo
import com.akabc.ngxmobileclient.net.systeminfo.GetMemInfo
import com.akabc.ngxmobileclient.net.systemstatus.GetCpuUsageInfo
import com.akabc.ngxmobileclient.net.systemstatus.GetDiskUsageInfo
import com.akabc.ngxmobileclient.net.systemstatus.GetMemUsageInfo
import com.akabc.ngxmobileclient.net.systemstatus.GetNetUsageInfo
import com.akabc.ngxmobileclient.ui.dashboard.SystemInfo
import com.akabc.ngxmobileclient.ui.login.data.Result
import com.akabc.ngxmobileclient.ui.login.data.model.Captcha
import com.akabc.ngxmobileclient.ui.login.data.model.User
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import java.time.Instant


class Repository {
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
                            activity,
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
        activity: Activity,
        mainViewModel: MainViewModel,
        isCheckLogin: Boolean,
    ) {
        // handle login
        LoginRequest().let {
            it.url("http://${loginUser.ip}:${loginUser.port}${activity.getString(R.string.login_url)}")
            it.getUser(loginUser, isCheckLogin)
            it.get(activity, mainViewModel)
        }
    }

    fun getCaptcha(activity: Activity, mainViewModel: MainViewModel) {
        // val queue = SingletonVolley.getInstance(requireActivity().applicationContext).requestQueue
        GetCaptcha().let {
            it.url("http://${user.ip}:${user.port}${activity.getString(R.string.captcha_id_url)}")
            it.get(activity, mainViewModel)
        }
    }

    fun getCaptchaImage(
        captchaId: String,
        activity: Activity,
        mainViewModel: MainViewModel,
    ) {
        val url2 = "http://${user.ip}:${user.port}${
            String.format(activity.getString(R.string.captcha_url),
                captchaId)
        }"
        val captchaImageRequest = ImageRequest(url2, { bitmap ->
            mainViewModel.setCaptcha(mainViewModel.captcha.value!!.copy(bitmap = bitmap))
        }, 240, 80, Bitmap.Config.RGB_565, { error ->
            Log.e(name, error.toString())
        })
        SingletonVolley.getInstance(activity).addToRequestQueue(captchaImageRequest)
    }

    fun setLoggedInUser(loggedInUser: User) {
        this.user = loggedInUser
    }


    /**
     * 向服务器请求系统信息
     * **/
    fun getSysInfo(activity: Activity, mainViewModel: MainViewModel){
        getBaseSysInfo(activity, mainViewModel)
        getBaseHardwareInfo(activity, mainViewModel)
        getBaseCpuInfo(activity, mainViewModel)
        getMemInfo(activity, mainViewModel)
    }

    private fun getBaseSysInfo(activity: Activity, mainViewModel: MainViewModel) {
        GetBaseSysInfo().let {
            it.url("http://${user.ip}:${user.port}${activity.getString(R.string.sys_info_url)}")
            it.get(activity, mainViewModel)
        }

    }

    private fun getBaseHardwareInfo(activity: Activity, mainViewModel: MainViewModel) {
        GetBaseHardwareInfo().let {
            it.url("http://${user.ip}:${user.port}${activity.getString(R.string.pc_info_url)}")
            it.get(activity, mainViewModel)
        }
    }

    private fun getBaseCpuInfo(activity: Activity, mainViewModel: MainViewModel) {
        GetBaseCpuInfo().let {
            it.url("http://${user.ip}:${user.port}${activity.getString(R.string.cpu_info_url)}")
            it.get(activity, mainViewModel)
        }
    }

    private fun getMemInfo(activity: Activity, mainViewModel: MainViewModel) {
        GetMemInfo().let {
            it.url("http://${user.ip}:${user.port}${activity.getString(R.string.mem_info_url)}")
            it.get(activity, mainViewModel)
        }
    }

    /**
     * 请求系统状态信息
     * **/
    fun getUsageInfo(activity: Activity, mainViewModel: MainViewModel) {
        getCpuUsageInfo(activity, mainViewModel)
        getMemUsageInfo(activity, mainViewModel)
        getDiskUsageInfo(activity, mainViewModel)
        getNetUsageInfo(activity, mainViewModel)
    }

    private fun getCpuUsageInfo(activity: Activity, mainViewModel: MainViewModel) {
        GetCpuUsageInfo().let {
            it.url("http://${user.ip}:${user.port}${activity.getString(R.string.cpu_usage_url)}")
            it.get(activity, mainViewModel)
        }
    }

    private fun getMemUsageInfo(activity: Activity, mainViewModel: MainViewModel) {
        GetMemUsageInfo().let {
            it.url("http://${user.ip}:${user.port}${activity.getString(R.string.mem_usage_url)}")
            it.get(activity, mainViewModel)
        }
    }

    private fun getDiskUsageInfo(activity: Activity, mainViewModel: MainViewModel) {
        GetDiskUsageInfo().let {
            it.url("http://${user.ip}:${user.port}${activity.getString(R.string.disk_usage_url)}")
            it.get(activity, mainViewModel)
        }
    }

    private fun getNetUsageInfo(activity: Activity, mainViewModel: MainViewModel) {
        val url = "http://${user.ip}:${user.port}${activity.getString(R.string.net_usage_url)}"
        GetNetUsageInfo().let {
            it.url("http://${user.ip}:${user.port}${activity.getString(R.string.net_usage_url)}")
            it.get(activity, mainViewModel)
        }
    }
}