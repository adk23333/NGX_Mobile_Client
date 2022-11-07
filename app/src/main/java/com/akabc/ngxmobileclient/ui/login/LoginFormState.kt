package com.akabc.ngxmobileclient.ui.login


/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val ipError: Int? = null,
    val portError: Int? = null,
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false,
)