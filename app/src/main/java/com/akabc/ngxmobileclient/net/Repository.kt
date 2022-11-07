package com.akabc.ngxmobileclient.net

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.R
import com.akabc.ngxmobileclient.ui.dashboard.SystemInfo
import com.akabc.ngxmobileclient.ui.login.data.Result
import com.akabc.ngxmobileclient.ui.login.data.model.Captcha
import com.akabc.ngxmobileclient.ui.login.data.model.LoggedInUser
import java.time.Instant

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class Repository {
    private val name = this.toString()

    private var systemInfo = SystemInfo()

    // in-memory cache of the loggedInUser object
    var user = LoggedInUser()

    val isLoggedIn: Boolean
        get() = user.token != null


    fun checkLonginStatus(activity: Activity, mainViewModel: MainViewModel) {
        Thread {
            while (true) {
                user.token?.let {
                    val temp = user.exceptionTime - Instant.now().epochSecond
                    if (temp in 0..299 && user.captcha.ctId != null && user.captcha.ctCode != null) {
                        login(user.displayName,
                            user.pwd,
                            user.captcha.ctId!!,
                            user.captcha.ctCode!!,
                            user.ip,
                            user.port,
                            activity,
                            mainViewModel)
                    }
                    if (temp < 0 && user.captcha.ctId != null && user.captcha.ctCode != null) {
                        login(user.displayName,
                            user.pwd,
                            user.captcha.ctId!!,
                            user.captcha.ctCode!!,
                            user.ip,
                            user.port,
                            activity,
                            mainViewModel)
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
        username: String,
        password: String,
        captchaId: String,
        captcha: String,
        ip: String,
        port: String,
        activity: Activity,
        mainViewModel: MainViewModel,
    ) {
        // handle login
        val url = "http://$ip:$port${activity.getString(R.string.login_url)}"
        val pwd = RequestKit().md5(password)
        val body = RequestKit().toJSONObject(
            "UserName" to username,
            "Password" to pwd,
            "CaptchaId" to captchaId,
            "CaptchaCode" to captcha,
            "PasswordCipher" to "MD5"
        )
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
                        LoggedInUser(uid,
                            username,
                            token,
                            expirationTime,
                            pwd,
                            Captcha(captchaId, captcha),
                            ip,
                            port)
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
        // TODO
        val url1 =
            "http://${user.ip}:${user.port}${activity.getString(R.string.captcha_id_url)}"
        var captchaId: String
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url1, null,
            { response ->
                Log.d(name, response.toString())
                captchaId = response.getJSONObject("Data").getString("CaptchaId")
                mainViewModel.setCaptcha(Captcha(captchaId))
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
        val url2 = "http://${user.ip}:${user.port}${String.format(activity.getString(R.string.captcha_url), captchaId)}"
        val captchaImageRequest = ImageRequest(url2, { bitmap ->
            mainViewModel.setCaptcha(Captcha(bitmap = bitmap))
            Log.e("dd","hhh")
        }, 240, 80, Bitmap.Config.RGB_565, { error ->
            Log.e(name, error.toString())
        })
        SingletonVolley.getInstance(activity).addToRequestQueue(captchaImageRequest)
    }

    fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    fun getBaseSysInfo(activity: Activity, mainViewModel: MainViewModel) {
        val url = activity.getString(R.string.url) + activity.getString(R.string.sys_info_url)
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
                    mainViewModel.setSysBaseInfo(systemInfo)
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

    private fun getBaseHardwareInfo(activity: Activity, mainViewModel: MainViewModel)    {
        val url = activity.getString(R.string.url) + activity.getString(R.string.pc_info_url)
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, null,
            { response ->
                try {
                    Log.d(name, response.toString())
                    val data = response.getJSONObject("Data")
                    val info = data.getJSONObject("info")
                    systemInfo.platform = info.getString("platform")
                    systemInfo.platformVersion = info.getString("platformVersion")
                    systemInfo.kernelArch = info.getString("kernelArch")
                    mainViewModel.setSysBaseInfo(systemInfo)
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
            activity.getString(R.string.url) + activity.getString(R.string.cpu_info_url)
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, null,
            { response ->
                try {
                    Log.d(name, response.toString())
                    val data = response.getJSONArray("Data")
                    systemInfo.coreNum = data.length()
                    systemInfo.cpuName = data.getJSONObject(0).getString("modelName")
                    mainViewModel.setSysBaseInfo(systemInfo)
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
        val url = activity.getString(R.string.url) + activity.getString(R.string.mem_info_url)
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
                    mainViewModel.setSysBaseInfo(systemInfo)
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