package com.akabc.ngxmobileclient.net.login

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest

class GetCaptcha: BaseRequest() {
    override var tag: String = this.toString()

    lateinit var url: String
    lateinit var activity: Activity
    lateinit var mainViewModel: MainViewModel

    operator fun invoke(block:GetCaptcha.() -> Unit) {
        block()
        super.request(url, activity, { response ->
            Log.d(tag, response.toString())
            val captchaId = response.getJSONObject("Data").getString("CaptchaId")
            mainViewModel.setCaptcha(mainViewModel.captcha.value!!.copy(ctId = captchaId))
            mainViewModel.captcha.value!!.ctId?.let { mainViewModel.repository.getCaptchaImage(it, activity, mainViewModel) }
        },
            { error ->
                Log.e(tag, error.toString())
            }
        ) {
            null
        }
    }

    fun url(url: String){
        this.url = url
    }
    fun activity(activity: Activity){
        this.activity = activity
    }
    fun mainViewModel(mainViewModel: MainViewModel){
        this.mainViewModel = mainViewModel
    }

}