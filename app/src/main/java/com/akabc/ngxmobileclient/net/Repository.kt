package com.akabc.ngxmobileclient.net

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.R
import com.akabc.ngxmobileclient.ui.dashboard.DiskUsageInfo
import com.akabc.ngxmobileclient.ui.dashboard.MemUsageInfo
import com.akabc.ngxmobileclient.ui.dashboard.NetUsageInfo
import com.akabc.ngxmobileclient.ui.dashboard.SystemInfo
import com.akabc.ngxmobileclient.ui.login.data.Result
import com.akabc.ngxmobileclient.ui.login.data.model.Captcha
import com.akabc.ngxmobileclient.ui.login.data.model.User
import java.time.Instant


class Repository {
    private val name = this.toString()

    private var systemInfo = SystemInfo()

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
        val url =
            "http://${loginUser.ip}:${loginUser.port}${activity.getString(R.string.login_url)}"
        val pwd = if (isCheckLogin){
            loginUser.pwd
        }else{
            RequestKit().md5(loginUser.pwd)
        }

        val body = RequestKit().toJSONObject(
            "UserName" to loginUser.displayName,
            "Password" to pwd,
            "CaptchaId" to loginUser.captcha.ctId,
            "CaptchaCode" to loginUser.captcha.ctCode,
            "PasswordCipher" to "MD5"
        )
        Log.e(name, body.toString())
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, body,
            { response ->
                Log.d(name, response.toString())
                try {
                    val data = response.getJSONObject("Data")
                    val token = data.getString("Token")
                    val userData = data.getJSONObject("UserData")
                    val uid = userData.getString("Id")
                    val expirationTime = data.getInt("ExpiresAt")

                    val fakeUser =
                        User(uid,
                            loginUser.displayName,
                            token,
                            expirationTime,
                            pwd,
                            Captcha(loginUser.captcha.ctId, loginUser.captcha.ctCode),
                            loginUser.ip,
                            loginUser.port)
                    val result = Result.Success(fakeUser)
                    setLoggedInUser(result.data)
                    mainViewModel.setLoginResult(result)
                } catch (e: Exception) {
                    val failMsg = FailMsg("Nothing", 0)
                    failMsg.msg = response.getString("Message")
                    failMsg.code = response.getInt("Code")
                    mainViewModel.setLoginResult(Result.Fail(failMsg))
                }

            },
            { error ->
                Log.e(name, error.toString())
                mainViewModel.setLoginResult(Result.Error(error))
            }
        )
        SingletonVolley.getInstance(activity.applicationContext)
            .addToRequestQueue(jsonObjectRequest)
    }

    fun getCaptcha(activity: Activity, mainViewModel: MainViewModel) {
        // val queue = SingletonVolley.getInstance(requireActivity().applicationContext).requestQueue
        val url1 =
            "http://${user.ip}:${user.port}${activity.getString(R.string.captcha_id_url)}"
        var captchaId: String
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url1, null,
            { response ->
                Log.d(name, response.toString())
                captchaId = response.getJSONObject("Data").getString("CaptchaId")
                mainViewModel.setCaptcha(mainViewModel.captcha.value!!.copy(ctId = captchaId))
                getCaptchaImage(captchaId, activity, mainViewModel)
            },
            { error ->
                Log.e(name, error.toString())
            }
        )
        SingletonVolley.getInstance(activity).addToRequestQueue(jsonObjectRequest)
    }

    private fun getCaptchaImage(
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
    fun getBaseSysInfo(activity: Activity, mainViewModel: MainViewModel) {
        val url = "http://${user.ip}:${user.port}${activity.getString(R.string.sys_info_url)}"
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, null,
            { response ->
                try {
                    Log.d(name, response.toString())
                    getBaseHardwareInfo(activity, mainViewModel)
                    getBaseCpuInfo(activity, mainViewModel)
                    getMemInfo(activity, mainViewModel)
                    val data = response.getJSONObject("Data")
                    systemInfo.channel = data.getString("Channel")
                    systemInfo.proto = data.getString("Proto")
                    systemInfo.major = data.getString("Major")
                    systemInfo.minor = data.getString("Minor")
                    systemInfo.forBoard = data.getString("ForBoard")
                    systemInfo.buildInfo = data.getString("BuildInfo")
                    systemInfo.buildTime = data.getString("BuildTime")
                    mainViewModel.sysBaseInfo(systemInfo)
                } catch (e: Exception) {
                    Log.w(name, e.toString())
                }
            },
            { error ->
                Log.d(name, error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                mainViewModel.loginResult.value?.success?.let {
                    return mutableMapOf("Authorization" to it.token!!)
                }
                return super.getHeaders()
            }
        }
        SingletonVolley.getInstance(activity.applicationContext)
            .addToRequestQueue(jsonObjectRequest)

    }

    private fun getBaseHardwareInfo(activity: Activity, mainViewModel: MainViewModel) {
        val url = "http://${user.ip}:${user.port}${activity.getString(R.string.pc_info_url)}"
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, null,
            { response ->
                try {
                    Log.d(name, response.toString())
                    val data = response.getJSONObject("Data")
                    val info = data.getJSONObject("info")
                    systemInfo.platform = info.getString("platform")
                    systemInfo.platformVersion = info.getString("platformVersion")
                    systemInfo.kernelArch = info.getString("kernelArch")
                    mainViewModel.sysBaseInfo(systemInfo)
                } catch (e: Exception) {
                    Log.w(name, e.toString())
                }
            },
            { error ->
                Log.d(name, error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                mainViewModel.loginResult.value?.success?.let {
                    return mutableMapOf("Authorization" to it.token!!)
                }
                return super.getHeaders()
            }
        }

        SingletonVolley.getInstance(activity.applicationContext)
            .addToRequestQueue(jsonObjectRequest)
    }

    private fun getBaseCpuInfo(activity: Activity, mainViewModel: MainViewModel) {
        val url =
            "http://${user.ip}:${user.port}${activity.getString(R.string.cpu_info_url)}"
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, null,
            { response ->
                try {
                    Log.d(name, response.toString())
                    val data = response.getJSONArray("Data")
                    systemInfo.coreNum = data.length()
                    systemInfo.cpuName = data.getJSONObject(0).getString("modelName")
                    mainViewModel.sysBaseInfo(systemInfo)
                } catch (e: Exception) {
                    Log.w(name, e.toString())
                }
            },
            { error ->
                Log.d(name, error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                mainViewModel.loginResult.value?.success?.let {
                    return mutableMapOf("Authorization" to it.token!!)
                }
                return super.getHeaders()
            }
        }

        SingletonVolley.getInstance(activity.applicationContext)
            .addToRequestQueue(jsonObjectRequest)
    }

    private fun getMemInfo(activity: Activity, mainViewModel: MainViewModel) {
        val url = "http://${user.ip}:${user.port}${activity.getString(R.string.mem_info_url)}"
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, null,
            { response ->
                try {
                    Log.d(name, response.toString())
                    val data = response.getJSONObject("Data")
                    val mem = data.getJSONArray("children")
                    val memLen = mem.length()
                    var i = 0
                    systemInfo.memSumSize = 0
                    while (i < memLen) {
                        val m = mem.getJSONObject(i)
                        systemInfo.memSumSize += m.getLong("size")
                        i++
                    }
                    mainViewModel.sysBaseInfo(systemInfo)
                } catch (e: Exception) {
                    Log.w(name, e.toString())
                }
            },
            { error ->
                Log.d(name, error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                mainViewModel.loginResult.value?.success?.let {
                    return mutableMapOf("Authorization" to it.token!!)
                }
                return super.getHeaders()
            }
        }

        SingletonVolley.getInstance(activity.applicationContext)
            .addToRequestQueue(jsonObjectRequest)
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
        val url = "http://${user.ip}:${user.port}${activity.getString(R.string.cpu_usage_url)}"
        val body = RequestKit().toJSONObject(
            "Offset" to 0,
            "Limit" to 1000,
            "Interval" to 0,
            "Percpu" to true
        )

        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, body,
            { response ->
                try {
                    // Log.d(name, response.toString())
                    val data = response.getJSONArray("Data")
                    val cpUsage = mutableListOf<Double>()
                    for (i in 0 until data.length()) {
                        Log.d(name, data.getDouble(i).toString())
                        cpUsage.add(data.getDouble(i))
                    }
                    mainViewModel.usageInfo(mainViewModel.usageInfo.value!!.copy(cpUsageInfo = cpUsage))
                } catch (e: Exception) {
                    Log.w(name, e.toString())
                }
            },
            { error ->
                Log.d(name, error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                mainViewModel.loginResult.value?.success?.let {
                    return mutableMapOf("Authorization" to it.token!!)
                }
                return super.getHeaders()
            }
        }

        SingletonVolley.getInstance(activity.applicationContext)
            .addToRequestQueue(jsonObjectRequest)
    }

    private fun getMemUsageInfo(activity: Activity, mainViewModel: MainViewModel) {
        val url = "http://${user.ip}:${user.port}${activity.getString(R.string.mem_usage_url)}"
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, null,
            { response ->
                try {
                    Log.d(name, response.toString())
                    val data = response.getJSONObject("Data")
                    val memPercent = data.getDouble("usedPercent")
                    val memUsed = data.getLong("used")
                    mainViewModel.usageInfo(mainViewModel.usageInfo.value!!.copy(memUsageInfo = MemUsageInfo(
                        memPercent,
                        memUsed)))
                } catch (e: Exception) {
                    Log.w(name, e.toString())
                }
            },
            { error ->
                Log.d(name, error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                mainViewModel.loginResult.value?.success?.let {
                    return mutableMapOf("Authorization" to it.token!!)
                }
                return super.getHeaders()
            }
        }

        SingletonVolley.getInstance(activity.applicationContext)
            .addToRequestQueue(jsonObjectRequest)
    }

    private fun getDiskUsageInfo(activity: Activity, mainViewModel: MainViewModel) {
        val url = "http://${user.ip}:${user.port}${activity.getString(R.string.disk_usage_url)}"
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, null,
            { response ->
                try {
                    Log.d(name, response.toString())
                    val data = response.getJSONArray("Data")
                    val disks = mutableListOf<DiskUsageInfo>()
                    for (i in 0 until data.length()) {
                        val disk = data.getJSONObject(i)
                        disks.add(DiskUsageInfo(disk.getLong("Used"), disk.getLong("Size")))
                    }
                    mainViewModel.usageInfo(mainViewModel.usageInfo.value!!.copy(diskUsageInfo = disks))
                } catch (e: Exception) {
                    Log.w(name, e.toString())
                }
            },
            { error ->
                Log.d(name, error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                mainViewModel.loginResult.value?.success?.let {
                    return mutableMapOf("Authorization" to it.token!!)
                }
                return super.getHeaders()
            }
        }

        SingletonVolley.getInstance(activity.applicationContext)
            .addToRequestQueue(jsonObjectRequest)
    }

    private fun getNetUsageInfo(activity: Activity, mainViewModel: MainViewModel) {
        val url = "http://${user.ip}:${user.port}${activity.getString(R.string.net_usage_url)}"
        val body = RequestKit().toJSONObject(
            "Offset" to 0,
            "Limit" to 1000,
            "Pernic" to true
        )
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, body,
            { response ->
                try {
                    Log.d(name, response.toString())
                    val data = response.getJSONArray("Data")
                    val time = response.getLong("Time")
                    val nets = mutableListOf<NetUsageInfo>()
                    for (i in 0 until data.length()) {
                        val net = data.getJSONObject(i)
                        val sent = net.getLong("bytesSent")
                        val recv = net.getLong("bytesRecv")
                        if (mainViewModel.usageInfo.value!!.netUsageInfo.size != data.length()) {
                            nets.add(NetUsageInfo(sent, recv, 1, 1, 1, 1))
                        } else {
                            nets.add(NetUsageInfo(sent, recv,
                                sent - mainViewModel.usageInfo.value!!.netUsageInfo[i].bytesSent,
                                recv - mainViewModel.usageInfo.value!!.netUsageInfo[i].bytesRecv,
                                time,
                                time - mainViewModel.usageInfo.value!!.netUsageInfo[i].time))
                        }
                    }
                    mainViewModel.usageInfo(mainViewModel.usageInfo.value!!.copy(netUsageInfo = nets))
                } catch (e: Exception) {
                    Log.w(name, e.toString())
                }
            },
            { error ->
                Log.d(name, error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                mainViewModel.loginResult.value?.success?.let {
                    return mutableMapOf("Authorization" to it.token!!)
                }
                return super.getHeaders()
            }
        }

        SingletonVolley.getInstance(activity.applicationContext)
            .addToRequestQueue(jsonObjectRequest)
    }
}