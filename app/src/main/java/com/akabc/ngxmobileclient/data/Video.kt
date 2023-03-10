package com.akabc.ngxmobileclient.data

import android.graphics.Bitmap
import org.jetbrains.annotations.Nullable

data class Video(
    val Id: String,
    val Vid: String,
    val Uid: Int,
    val Gid: Int,
    val Size: Long,
    val Perm: String,
    val Atim: Long,
    val Ctim: Long,
    val Mtim: Long,
    val Type: String,
    val Ext: String,
    val Name: String,
    val Path: String,
    val cover: Bitmap? = null,
    val details: VideoDetails? = null,
)
