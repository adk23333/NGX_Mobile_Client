package com.akabc.ngxmobileclient.net.login

import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.akabc.ngxmobileclient.net.SingletonVolley

class GetCaptcha(
    val url: String,
    val captchaImageUrl: String,
    val singletonVolley: SingletonVolley,
    val mainViewModel: MainViewModel
): BaseRequest() {
    override var tag: String = this.toString()

    operator fun invoke() {
        super.request(url, singletonVolley, { response ->
            val captchaId = response.getJSONObject("Data").getString("CaptchaId")
            mainViewModel.setCaptcha(mainViewModel.captcha.value!!.copy(ctId = captchaId))
            mainViewModel.captcha.value!!.ctId?.let {
                mainViewModel.repository.getCaptchaImage(it, captchaImageUrl, mainViewModel)
            }
        }, { error ->
                Log.e(tag, error.toString())
            }
        ) {
            null
        }
    }

    init {
        invoke()
    }

}