package com.akabc.ngxmobileclient.ui.dashboard

data class SystemInfo(
    var channel: String = "",
    var proto: String = "",
    var major: String = "",
    var minor: String = "",
    var forBoard: String = "",
    var buildInfo: String = "",
    var buildTime: String = "",
    var platform: String = "",
    var platformVersion: String = "",
    var kernelArch: String = "",
    var cpuName: String = "",
    var coreNum: Int = 0,
    var memSumSize: Long = 0,
)