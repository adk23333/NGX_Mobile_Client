package com.akabc.ngxmobileclient

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akabc.ngxmobileclient.net.Repository
import com.akabc.ngxmobileclient.ui.dashboard.SystemInfo
import com.akabc.ngxmobileclient.ui.login.LoginFormState
import com.akabc.ngxmobileclient.ui.login.LoginResult
import com.akabc.ngxmobileclient.ui.login.data.Result
import com.akabc.ngxmobileclient.ui.login.data.model.Captcha
import com.akabc.ngxmobileclient.ui.login.data.model.User
import java.lang.Exception
import java.util.regex.Pattern


class MainViewModel(val repository: Repository) : ViewModel() {
    private val name = this.toString()


    // Login Fragment
    private val _captcha = MutableLiveData<Captcha>()
    val captcha: LiveData<Captcha> = _captcha.apply {
        this.value = Captcha()
    }

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(
        loginUser: User,
        activity: Activity,
    ) {
        repository.login(
            loginUser,
            activity,
            this)
    }

    fun setLoginResult(result: Result<User>) {
        when (result) {
            is Result.Success -> {
                _loginResult.value =
                    LoginResult(success = result.data)
            }
            is Result.Fail -> {
                _loginResult.value = LoginResult(fail = result.msg)
            }
            else -> {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun setLoginResultForRecord(activity: Activity, user: User) {
        _loginResult.value = LoginResult(success = user)
        repository.setLoggedInUser(user)
        repository.checkLonginStatus(activity, this)
    }

    fun setLoginResultIp(activity: Activity, user: User) {
        _loginResult.value = LoginResult(success = user)
    }

    fun setCaptcha(captcha: Captcha) {
        captcha.ctId?.let { _captcha.value = Captcha(captcha.ctId, this.captcha.value?.ctCode, this.captcha.value?.bitmap) }
        captcha.ctCode?.let { _captcha.value = Captcha(this.captcha.value?.ctId, captcha.ctCode, this.captcha.value?.bitmap) }
        captcha.bitmap?.let { _captcha.value = Captcha(this.captcha.value?.ctId, this.captcha.value?.ctCode, captcha.bitmap) }
        Log.d(name, captcha.toString())
    }

    fun loginDataChanged(ip: String, port: String, username: String, password: String) {
        if (!isPortValid(port)) {
            _loginForm.value = LoginFormState(portError = R.string.invalid_port)
        } else if (!isIpValid(ip)) {
            _loginForm.value = LoginFormState(ipError = R.string.invalid_ip)
        } else if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isIpValid(ip: String): Boolean {
        val regex =
            """(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?(([0-9]{1,3}\.){3}[0-9]{1,3}|([0-9a-z_!~*'()-]+\.)*([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\.[a-z]{2,6})((/?)|(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$"""
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(ip)
        return matcher.matches()
    }

    private fun isPortValid(port: String): Boolean {
        return try {
            val mPort = port.toInt()
            mPort in 0..65535
        } catch (e: Exception) {
            Log.d(name, e.toString())
            false
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    /**
     * System Base Info
     * **/

    private val _sysBaseInfo = MutableLiveData<SystemInfo>()
    val sysBaseInfo: LiveData<SystemInfo> = _sysBaseInfo
    fun setSysBaseInfo(systemInfo: SystemInfo) {
        _sysBaseInfo.value = systemInfo
    }
}