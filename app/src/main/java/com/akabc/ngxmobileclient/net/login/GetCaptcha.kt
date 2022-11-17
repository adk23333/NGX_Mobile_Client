package com.akabc.ngxmobileclient.net.login

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.net.BaseRequest
import com.android.volley.VolleyError
import org.json.JSONObject

class GetCaptcha: BaseRequest() {
    override var tag: String = this.toString()

    override fun onSuccess(activity: Activity, mainViewModel: MainViewModel, response: JSONObject) {
        Log.d(tag, response.toString())
        val captchaId = response.getJSONObject("Data").getString("CaptchaId")
        mainViewModel.setCaptcha(mainViewModel.captcha.value!!.copy(ctId = captchaId))
        mainViewModel.captcha.value!!.ctId?.let { mainViewModel.repository.getCaptchaImage(it, activity, mainViewModel) }
    }

    override fun onError(error: VolleyError, mainViewModel: MainViewModel) {
        Log.e(tag, error.toString())
    }

    override fun headers(mainViewModel: MainViewModel): MutableMap<String, String>? {
        return null
    }
}