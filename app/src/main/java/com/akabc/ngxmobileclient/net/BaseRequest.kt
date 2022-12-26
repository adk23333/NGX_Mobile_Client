package com.akabc.ngxmobileclient.net

import android.util.Log
import com.android.volley.Request.Method
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

abstract class BaseRequest {
    abstract var tag: String
    val method = Method.POST

    open var body: JSONObject? = null
    fun request(
        url: String,
        requestQueue: SingletonVolley,
        onSuccess: (JSONObject) -> Unit,
        onError: (VolleyError) -> Unit,
        headers: () -> MutableMap<String, String>?,
    ) {
        val jsonObjectRequest = object : JsonObjectRequest(method, url, body,
            { response ->
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
        requestQueue.addToRequestQueue(jsonObjectRequest)
    }

    fun toJSONObject(vararg params: Pair<String, Any?>): JSONObject {
        val param = JSONObject()
        for (i in params) {
            if (i.second is Set<*>) {
                for (j in i.second as Set<*>) {
                    param.append(i.first, j)
                }
            } else {
                param.put(i.first, i.second)
            }
        }
        return param
    }
}