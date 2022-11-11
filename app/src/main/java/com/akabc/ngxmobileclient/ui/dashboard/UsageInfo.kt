package com.akabc.ngxmobileclient.ui.dashboard

data class UsageInfo (
    var cpUsageInfo: List<Double> = listOf(0.0),
    var memUsageInfo: MemUsageInfo = MemUsageInfo(),
    var diskUsageInfo: List<DiskUsageInfo> = listOf(DiskUsageInfo()),
)

data class MemUsageInfo(
    var percent: Double = 0.0,
    var usedSize: Long = 0,
)

data class DiskUsageInfo(
    var usedSize: Long = 0,
    var size: Long = 0,
)
