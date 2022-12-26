package com.akabc.ngxmobileclient.data

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
)
