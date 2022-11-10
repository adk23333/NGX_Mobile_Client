package com.akabc.ngxmobileclient.ui.dashboard

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.databinding.FragmentDashboardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.logging.Logger
import kotlin.concurrent.schedule

class DashboardFragment : Fragment() {
    val name = this.tag
    private var _binding: FragmentDashboardBinding? = null

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


        val pgBar = binding.pgBarCpu
        val tvCpu = binding.tvCpu


        mainViewModel.sysBaseInfo.observe(viewLifecycleOwner) {
            refSysBaseInfo(it)
        }
        mainViewModel.loginResult.observe(viewLifecycleOwner){
            mainViewModel.repository.getBaseSysInfo(requireActivity(), mainViewModel)
        }
        mainViewModel.cpUsage.observe(viewLifecycleOwner){ list ->
            var sum: Double = 0.0
            list.forEach{
                sum += it
            }
            Log.e(name, sum.toString())
            val cpUsage = sum / 4.0
            pgBar.progress = cpUsage.toInt()
            tvCpu.text = String.format("cpu \n%.1f%%", cpUsage)
        }
//        CoroutineScope(Dispatchers.IO).launch {
//
//        }

        val period: Long = 1000
        Timer().schedule(500, period) {
            mainViewModel.repository.getCpuUsageInfo(requireActivity(), mainViewModel)
        }

        return binding.root
    }

    private fun refSysBaseInfo(it: SystemInfo){
        binding.tvSystemInfo.text = "${it.platform} ${it.platformVersion} ${it.kernelArch}"
        binding.tvCpuInfo.text = "${it.coreNum}h ${it.memSumSize.ushr(30)}g ${it.cpuName}"
        binding.tvNgxInfo1.text =
            "ver.${it.proto}.${it.major}.${it.minor} ${it.forBoard} ${it.channel}"
        binding.tvNgxInfo2.text = it.buildInfo
        binding.tvNgxInfo3.text = it.buildTime
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}