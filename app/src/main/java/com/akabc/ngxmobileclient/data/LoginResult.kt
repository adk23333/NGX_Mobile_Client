package com.akabc.ngxmobileclient.data

import com.akabc.ngxmobileclient.net.FailMsg

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: User? = null,
    val error: Int? = null,
    val fail: FailMsg? = null
)