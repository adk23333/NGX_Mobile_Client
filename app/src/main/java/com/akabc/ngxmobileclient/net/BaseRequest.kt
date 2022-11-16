package com.akabc.ngxmobileclient.net

import android.app.Activity
import android.util.Log
import com.akabc.ngxmobileclient.MainViewModel
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

abstract class BaseRequest {
    var tag = this.toString()
    lateinit var url: String

    abstract var body: JSONObject
    fun get(activity: Activity, mainViewModel: MainViewModel){
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, body,
            { response ->
                Log.d(tag, response.toString())
                onSuccess(activity, mainViewModel, response)
            },
            { error ->
                Log.d(tag, error.toString())
                onError(error)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                setHeaders(mainViewModel)?.let {
                    return it
                }
                return super.getHeaders()
            }
        }

        SingletonVolley.getInstance(activity.applicationContext)
            .addToRequestQueue(jsonObjectRequest)
    }

    abstract fun onSuccess(activity: Activity, mainViewModel: MainViewModel, response: JSONObject)
    abstract fun onError(error: VolleyError)
    abstract fun setHeaders(mainViewModel: MainViewModel):MutableMap<String, String>?
    fun url(url: String){
        this.url = url
    }
}