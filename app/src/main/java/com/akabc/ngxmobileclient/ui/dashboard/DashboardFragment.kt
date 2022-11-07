package com.akabc.ngxmobileclient.ui.dashboard

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

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
        var i=0
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                // set the limitations for the numeric
                // text under the progress bar
                if (i <= 100) {
                    pgBar.progress = i
                    i++
                    handler.postDelayed(this, 200)
                } else {
                    handler.removeCallbacks(this)
                }
            }
        }, 200)

        mainViewModel.sysBaseInfo.observe(viewLifecycleOwner) {
            refSysBaseInfo(it)
        }
        mainViewModel.loginResult.observe(viewLifecycleOwner){
            mainViewModel.repository.getBaseSysInfo(requireActivity(), mainViewModel)
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