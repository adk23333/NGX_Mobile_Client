package com.akabc.ngxmobileclient.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.R
import com.akabc.ngxmobileclient.databinding.FragmentDashboardBinding
import com.akabc.ngxmobileclient.net.diskDataFormat
import com.akabc.ngxmobileclient.net.formatBy
import com.akabc.ngxmobileclient.net.memDataFormat
import com.akabc.ngxmobileclient.net.v
import java.util.*
import kotlin.concurrent.schedule

class DashboardFragment : Fragment() {
    val name = this.tag
    private var _binding: FragmentDashboardBinding? = null
    private lateinit var timer: Timer
    private val mainViewModel: MainViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val homeViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)


        val pgBarCpu = binding.pgBarCpu
        val pgBarMem = binding.pgBarMem
        val pgBarDisk = binding.pgBarDisk
        val pgBarNet = binding.pgBarNet

        val tvCpu = binding.tvCpu
        val tvMem = binding.tvMem
        val tvDisk = binding.tvDisk
        val tvNetDown = binding.tvNetDown
        val tvNetUp = binding.tvNetUp

        mainViewModel.setFab(R.drawable.ic_user_24, 96)

        mainViewModel.sysBaseInfo.observe(viewLifecycleOwner) {
            refSysBaseInfo(it)
        }
        mainViewModel.loginResult.observe(viewLifecycleOwner) {
            mainViewModel.repository.getSysInfo(
                listOf(getString(R.string.sys_info_url),
                getString(R.string.pc_info_url),
                getString(R.string.cpu_info_url),
                getString(R.string.mem_info_url)), mainViewModel)
        }
        mainViewModel.usageInfo.observe(viewLifecycleOwner) { usage ->

            /** CPU **/
            usage.cpUsageInfo.let { list ->
                var sum = 0.0
                list.forEach {
                    sum += it
                }
                val cpUsage = sum / 4.0
                pgBarCpu.progress = cpUsage.toInt()
                tvCpu.text = String.format("%.1f%%", cpUsage)
            }

            /** Mem **/
            usage.memUsageInfo.let {
                pgBarMem.progress = it.percent.toInt()
                tvMem.text = it.percent v it.usedSize.memDataFormat formatBy "%.1f%%\n%s"
                //String.format("%.1f%%\n%s", it.percent, it.usedSize.memDataFormat)
            }

            /** Disk **/
            usage.diskUsageInfo.let { list ->
                var used: Long = 0
                var sum: Long = 1
                for (i in list) {
                    used += i.usedSize
                    sum += i.size
                }
                val pgUsage = ((used / sum.toDouble()) * 100)
                pgBarDisk.progress = pgUsage.toInt()
                tvDisk.text = String.format("%.1f%%\n%s", pgUsage, used.diskDataFormat)
            }

            /** Net **/
            usage.netUsageInfo.let { list ->
                var sentIncre: Long = 0
                var recvIncre: Long = 0
                var time: Long = 1000
                for (i in list) {
                    sentIncre += i.sentIncrement
                    recvIncre += i.recvIncrement
                    time = i.timeIncre
                }
                pgBarNet.progress = ((recvIncre.toDouble() / (recvIncre + sentIncre)) * 100).toInt()
                tvNetDown.text = String.format("↓%s", recvIncre.memDataFormat)
                tvNetUp.text =
                    String.format("↑%s", sentIncre.memDataFormat)
            }

        }
//        CoroutineScope(Dispatchers.IO).launch {
//
//        }

        return binding.root
    }

    private fun refSysBaseInfo(it: SystemInfo) {
        binding.tvSystemInfo.text = String.format(getString(R.string.tvSystemInfo),
            it.platform,
            it.platformVersion,
            it.kernelArch)
        binding.tvCpuInfo.text = String.format(getString(R.string.tvCpuInfo),
            it.coreNum,
            it.memSumSize.ushr(30),
            it.cpuName)
        binding.tvNgxInfo1.text = String.format(getString(R.string.tvNgxInfo1),
            it.proto,
            it.major,
            it.minor,
            it.forBoard,
            it.channel)

        binding.tvNgxInfo2.text = it.buildInfo
        binding.tvNgxInfo3.text = it.buildTime
    }

    override fun onResume() {
        super.onResume()
        val period: Long = 1000
        timer = Timer()
        timer.schedule(500, period) {
            mainViewModel.repository.getUsageInfo(
                listOf(getString(R.string.cpu_usage_url),
                    getString(R.string.mem_usage_url),
                    getString(R.string.disk_usage_url),
                    getString(R.string.net_usage_url)), mainViewModel)
        }
    }

    override fun onPause() {
        timer.cancel()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
        _binding = null
    }
}