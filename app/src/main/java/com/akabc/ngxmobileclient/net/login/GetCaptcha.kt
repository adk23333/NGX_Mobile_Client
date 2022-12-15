package com.akabc.ngxmobileclient.net.login

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest

class GetCaptcha: BaseRequest() {
    override var tag: String = this.toString()

    fun get(url:String, activity: Activity, mainViewModel: MainViewModel) {
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

}