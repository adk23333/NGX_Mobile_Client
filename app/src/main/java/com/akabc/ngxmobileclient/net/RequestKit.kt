package com.akabc.ngxmobileclient.net

import java.security.MessageDigest
import java.text.SimpleDateFormat

/** 按需求格式化 10位时间戳 **/
infix fun Long.dateFormat(pattern: String): String = SimpleDateFormat(pattern).format(this)


/** 格式化内存大小显示 **/
val Long.memDataFormat: String
    get() {
        val kb: Long = 1024
        val mb = kb.shl(10)
        val gb = mb.shl(10)
        val tb = gb.shl(10)

        return when (this) {
            in 0 until mb.shl(2) -> String.format("%.1fk", this.toDouble() / kb)
            in mb.shl(2) until gb.shl(1) -> String.format("%.1fM", this.toDouble() / mb)
            in gb.shr(1) until tb -> String.format("%.1fG", this.toDouble() / gb)
            else -> String.format("%.1fT", this.toDouble() / tb)
        }
    }

/** 格式化硬盘大小显示 **/
val Long.diskDataFormat: String
    get() {
        val mb: Long = 1024
        val gb = mb.shl(10)
        val tb = gb.shl(10)
        val pb = tb.shl(10)

        return when (this) {
            in 0 until gb -> String.format("%.1fM", this.toDouble() / mb)
            in gb until tb -> String.format("%.1fG", this.toDouble() / gb)
            in tb until pb -> String.format("%.1fT", this.toDouble() / tb)
            else -> String.format("%.1fP", this.toDouble() / pb)
        }
    }

/** md5加密 */
val String.MD5: String
    get() {
        val hash = MessageDigest.getInstance("MD5").digest(this.toByteArray())
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            var str = Integer.toHexString(b.toInt())
            if (b < 0x10) {
                str = "0$str"
            }
            hex.append(str.substring(str.length - 2))
        }
        arrayOf<Any>(1,2)

        return hex.toString()
    }

infix fun Array<Any?>.formatBy(pattern: String) = String.format(pattern, *this)

infix fun String.v(temp: Any?): Array<Any?> = arrayOf(this, temp)
infix fun Number.v(temp: Any?): Array<Any?> = arrayOf(this, temp)







