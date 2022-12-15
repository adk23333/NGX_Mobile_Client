package com.akabc.ngxmobileclient.net.login

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.FailMsg
import com.akabc.ngxmobileclient.net.RequestKit
import com.akabc.ngxmobileclient.ui.login.data.Result
import com.akabc.ngxmobileclient.ui.login.data.model.Captcha
import com.akabc.ngxmobileclient.ui.login.data.model.User
import org.json.JSONObject

class LoginRequest: BaseRequest() {
    override var tag: String = this.toString()

    private lateinit var loginUser:User
    private lateinit var pwd: String

    override var body: JSONObject? = null

    fun get(url:String, activity: Activity, mainViewModel: MainViewModel) {
        super.request(url, activity, { response ->
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
                Log.e(tag, error.toString())
                mainViewModel.setLoginResult(Result.Error(error))
            }
        ) {
            null
        }
    }

    fun getUser(loginUser: User, isCheckLogin: Boolean){
        this.loginUser = loginUser
        this.pwd = if (isCheckLogin){
            loginUser.pwd
        }else{
            RequestKit().md5(loginUser.pwd)
        }
        body = RequestKit().toJSONObject(
            "UserName" to loginUser.displayName,
            "Password" to pwd,
            "CaptchaId" to loginUser.captcha.ctId,
            "CaptchaCode" to loginUser.captcha.ctCode,
            "PasswordCipher" to "MD5"
        )
    }

}