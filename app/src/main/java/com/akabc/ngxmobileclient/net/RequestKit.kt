package com.akabc.ngxmobileclient.net

import org.json.JSONObject
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

    fun diskDataFormat(data: Long): String {
        val mb: Long = 1024
        val gb = mb.shl(10)
        val tb = gb.shl(10)
        val pb = tb.shl(10)

        return when (data) {
            in 0 until gb -> String.format("%.1fM", data.toDouble() / mb)
            in gb until tb -> String.format("%.1fG", data.toDouble() / gb)
            in tb until pb -> String.format("%.1fT", data.toDouble() / tb)
            else -> String.format("%.1fP", data.toDouble() / pb)
        }
    }

    fun memDataFormat(data: Long): String {
        val kb: Long = 1024
        val mb = kb.shl(10)
        val gb = mb.shl(10)
        val tb = gb.shl(10)

        return when (data) {
            in 0 until gb.shl(1) -> String.format("%.1fM", data.toDouble() / mb)
            in gb.shr(1) until tb -> String.format("%.1fG", data.toDouble() / gb)
            else -> String.format("%.1fT", data.toDouble() / tb)
        }
    }

}