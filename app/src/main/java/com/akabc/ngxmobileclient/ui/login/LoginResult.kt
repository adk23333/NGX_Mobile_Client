package com.akabc.ngxmobileclient.ui.login

import com.akabc.ngxmobileclient.net.FailMsg
import com.akabc.ngxmobileclient.ui.login.data.model.User

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: User? = null,
    val error: Int? = null,
    val fail: FailMsg? = null
)