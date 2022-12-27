package com.akabc.ngxmobileclient.net.login

import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.data.Result
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.FailMsg
import com.akabc.ngxmobileclient.net.MD5
import com.akabc.ngxmobileclient.net.SingletonVolley
import com.akabc.ngxmobileclient.data.Captcha
import com.akabc.ngxmobileclient.data.User
import org.json.JSONObject

class LoginRequest(
    val url: String,
    private val loginUser: User,
    private val isCheckLogin: Boolean,
    val singletonVolley: SingletonVolley,
    val mainViewModel: MainViewModel,
) : BaseRequest() {
    override var tag: String = this.toString()

    private lateinit var pwd: String

    override var body: JSONObject? = null

    operator fun invoke() {
        this.pwd = if (isCheckLogin) {
            loginUser.pwd
        } else {
            loginUser.pwd.MD5
        }
        body = toJSONObject(
            "UserName" to loginUser.Name,
            "Password" to pwd,
            "CaptchaId" to loginUser.captcha.ctId,
            "CaptchaCode" to loginUser.captcha.ctCode,
            "PasswordCipher" to "MD5"
        )
        super.request(url, singletonVolley, { response ->
            try {
                val data = response.getJSONObject("Data")
                val tid = data.getString("Id")
                val token = data.getString("Token")
                val userData = data.getJSONObject("UserData")
                val uid = userData.getString("Id")
                val expirationTime = data.getInt("ExpiresAt")

                val fakeUser =
                    User(uid,
                        loginUser.Name,
                        tid,
                        token,
                        expirationTime,
                        pwd,
                        Captcha(loginUser.captcha.ctId, loginUser.captcha.ctCode),
                        loginUser.ip,
                        loginUser.port)
                val result = Result.Success(fakeUser)
                mainViewModel.repository.setLoggedInUser(result.data)
                mainViewModel.setLoginResult(result)
            } catch (e: Exception) {
                val failMsg = FailMsg("Nothing", 0)
                failMsg.msg = response.getString("Message")
                failMsg.code = response.getInt("Code")
                mainViewModel.setLoginResult(Result.Fail(failMsg))
            }
        },
            { error ->
                Log.d(tag, error.toString())
                mainViewModel.setLoginResult(Result.Error(error))
            }
        ) {
            null
        }
    }

    init {
        invoke()
    }

}