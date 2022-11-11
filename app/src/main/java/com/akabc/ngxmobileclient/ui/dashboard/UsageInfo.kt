package com.akabc.ngxmobileclient.ui.dashboard

data class UsageInfo (
    var cpUsageInfo: List<Double> = listOf(0.0),
    var memUsageInfo: MemUsageInfo = MemUsageInfo(),
    var diskUsageInfo: List<DiskUsageInfo> = listOf(DiskUsageInfo()),
    var netUsageInfo: List<NetUsageInfo> = listOf(NetUsageInfo()),
)

data class MemUsageInfo(
    var percent: Double = 0.0,
    var usedSize: Long = 0,
)

data class DiskUsageInfo(
    var usedSize: Long = 0,
    var size: Long = 0,
)

data class NetUsageInfo(
    var bytesSent: Long = 0,
    var bytesRecv: Long = 0,
    var sentIncrement: Long = 0,
    var recvIncrement: Long = 0,
    var time: Long = 1,
    var timeIncre: Long = 1,
)
