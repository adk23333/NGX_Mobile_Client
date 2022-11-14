package com.akabc.ngxmobileclient.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.databinding.FragmentDashboardBinding
import com.akabc.ngxmobileclient.net.RequestKit
import java.util.*
import kotlin.concurrent.schedule

class DashboardFragment : Fragment() {
    val name = this.tag
    private var _binding: FragmentDashboardBinding? = null
    private val timer = Timer()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]
        val mainViewModel: MainViewModel by activityViewModels()
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


        mainViewModel.sysBaseInfo.observe(viewLifecycleOwner) {
            refSysBaseInfo(it)
        }
        mainViewModel.loginResult.observe(viewLifecycleOwner) {
            mainViewModel.repository.getBaseSysInfo(requireActivity(), mainViewModel)
        }
        mainViewModel.usageInfo.observe(viewLifecycleOwner) { usage ->
            Log.d(name, usage.toString())

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
                tvMem.text =
                    String.format("%.1f%%\n%s", it.percent, RequestKit().memDataFormat(it.usedSize))
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
                tvDisk.text =
                    String.format("%.1f%%\n%s", pgUsage, RequestKit().diskDataFormat(used))
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
                tvNetDown.text = String.format("↓%s",
                    RequestKit().memDataFormat(recvIncre))
                tvNetUp.text =
                    String.format("↑%s", RequestKit().memDataFormat(sentIncre))
            }

        }
//        CoroutineScope(Dispatchers.IO).launch {
//
//        }

        val period: Long = 1000
        timer.schedule(500, period) {
            mainViewModel.repository.getUsageInfo(requireActivity(), mainViewModel)
        }

        return binding.root
    }

    private fun refSysBaseInfo(it: SystemInfo) {
        binding.tvSystemInfo.text = "${it.platform} ${it.platformVersion} ${it.kernelArch}"
        binding.tvCpuInfo.text = "${it.coreNum}h ${it.memSumSize.ushr(30)}g ${it.cpuName}"
        binding.tvNgxInfo1.text =
            "ver.${it.proto}.${it.major}.${it.minor} ${it.forBoard} ${it.channel}"
        binding.tvNgxInfo2.text = it.buildInfo
        binding.tvNgxInfo3.text = it.buildTime
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
        _binding = null
    }
}