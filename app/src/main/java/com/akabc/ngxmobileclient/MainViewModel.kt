package com.akabc.ngxmobileclient

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.akabc.ngxmobileclient.data.Fab
import com.akabc.ngxmobileclient.net.Repository
import com.akabc.ngxmobileclient.ui.dashboard.SystemInfo
import com.akabc.ngxmobileclient.ui.dashboard.UsageInfo
import com.akabc.ngxmobileclient.data.LoginFormState
import com.akabc.ngxmobileclient.data.LoginResult
import com.akabc.ngxmobileclient.data.Result
import com.akabc.ngxmobileclient.data.Captcha
import com.akabc.ngxmobileclient.data.User
import java.util.regex.Pattern


class MainViewModel(val repository: Repository) : ViewModel() {
    private val tag = this.toString()

    private val _fab = MutableLiveData<Fab>()
    val fab:LiveData<Fab> = _fab.apply {
        this.value = Fab(R.drawable.ic_user_24, 96)
    }
    fun setFab(icon: Int, size: Int){
        _fab.value = Fab(icon, size)
    }

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
        url: String,
    ) {
        repository.login(
            loginUser,
            url,
            this,
            false)
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
        _captcha.value = captcha
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
            Log.d(tag, e.toString())
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
    val sysBaseInfo: LiveData<SystemInfo> = _sysBaseInfo.apply {
        this.value = SystemInfo()
    }
    fun sysBaseInfo(systemInfo: SystemInfo) {
        _sysBaseInfo.value = systemInfo
    }

    /**
     *  System usage info TODO
     * **/
    private val _usageInfo = MutableLiveData<UsageInfo>()
    val usageInfo: LiveData<UsageInfo> = _usageInfo.apply {
        this.value = UsageInfo()
    }
    fun usageInfo(usageInfo: UsageInfo){
        _usageInfo.value = usageInfo
    }
}