package com.akabc.ngxmobileclient.net

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.android.volley.Header
import com.android.volley.Request.Method
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

abstract class BaseRequest {
    abstract var tag: String
    private lateinit var url: String
    val method = Method.POST

    open var body: JSONObject? = null
    fun get(
        activity: Activity,
        mainViewModel: MainViewModel,
        onSuccess:(JSONObject)->Unit,
        onError: (VolleyError)->Unit,
        headers:()->MutableMap<String, String>?,
    ) {
        val jsonObjectRequest = object : JsonObjectRequest(method, url, body,
            { response ->
                Log.d(tag, response.toString())
                onSuccess(response)
            },
            { error ->
                Log.d(tag, error.toString())
                onError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                headers()?.let {
                    return it
                }
                return super.getHeaders()
            }
        }

        SingletonVolley.getInstance(activity.applicationContext)
            .addToRequestQueue(jsonObjectRequest)
    }

    //    abstract fun onSuccess(activity: Activity, mainViewModel: MainViewModel, response: JSONObject)
//    abstract fun onError(error: VolleyError, mainViewModel: MainViewModel)
//    abstract fun headers(mainViewModel: MainViewModel):MutableMap<String, String>?
    fun url(url: String) {
        this.url = url
    }
}