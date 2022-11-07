package com.akabc.ngxmobileclient.net

import androidx.annotation.Nullable
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.security.MessageDigest

class RequestKit {

    fun toJSONObject(vararg params: Pair<String, Any?>): JSONObject {
        val param = JSONObject()
        for (i in params) {
            val value = if (i.second == null) "" else i.second
            param.put(i.first, value)
        }
        return param
    }

    /** md5加密 */
    fun md5(content: String): String {
        val hash = MessageDigest.getInstance("MD5").digest(content.toByteArray())
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            var str = Integer.toHexString(b.toInt())
            if (b < 0x10) {
                str = "0$str"
            }
            hex.append(str.substring(str.length - 2))
        }
        return hex.toString()
    }


}