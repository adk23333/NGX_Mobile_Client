package com.akabc.ngxmobileclient.data

import com.akabc.ngxmobileclient.net.FailMsg
import java.lang.Exception

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T : Any> {

    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class Fail(val msg: FailMsg) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Fail -> "Fail[fail=$msg]"
        }
    }
}