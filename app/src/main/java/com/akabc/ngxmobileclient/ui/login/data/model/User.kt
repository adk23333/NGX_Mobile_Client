package com.akabc.ngxmobileclient.ui.login.data.model

import android.graphics.Bitmap

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class User(
    val userId: String = "",
    val displayName: String = "",
    var token: String? = null,
    val exceptionTime: Int = 1,
    val pwd: String = "",
    val captcha: Captcha = Captcha(),
    var ip: String = "",
    var port: String = "",
)
data class Captcha(
    var ctId: String? = null,
    var ctCode:String? = null,
    var bitmap: Bitmap? = null,
)
