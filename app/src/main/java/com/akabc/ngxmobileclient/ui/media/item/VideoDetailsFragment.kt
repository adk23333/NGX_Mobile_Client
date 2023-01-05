package com.akabc.ngxmobileclient.ui.media.item

import android.database.DataSetObservable
import android.database.DataSetObserver
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter.IGNORE_ITEM_VIEW_TYPE
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import androidx.fragment.app.activityViewModels
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.data.DlnaDevice
import com.akabc.ngxmobileclient.data.Video
import com.akabc.ngxmobileclient.databinding.DlnaDevicesSpinnerBinding
import com.akabc.ngxmobileclient.databinding.FragmentVpapBinding
import com.akabc.ngxmobileclient.net.dateFormat
import com.akabc.ngxmobileclient.net.diskDataFormat
import com.akabc.ngxmobileclient.net.filename
import com.akabc.ngxmobileclient.net.filetype
import com.akabc.ngxmobileclient.ui.media.MediaViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class VideoDetailsFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentVpapBinding? = null
    private val binding get() = _binding!!
    private lateinit var onPreviewListener: (ExoPlayer) -> Unit
    private lateinit var videoView: StyledPlayerView
    private lateinit var exoPlayer: ExoPlayer
    private val mediaViewModel: MediaViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    var position = 0
    private lateinit var video: Video
    private var isSpinnerFirstStart = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVpapBinding.inflate(inflater, container, false)
        videoView = binding.videoView
        val spinner = binding.appCompatSpinner


        mediaViewModel.videos.observe(this) { videos ->
            video = videos[position]
            video.let {
                binding.videoDetailsNestedScrollView.tvDetailsTitle2.text = it.Name.filename
                binding.videoDetailsNestedScrollView.tvDetailsAddr2.text = it.Path
                binding.videoDetailsNestedScrollView.tvDetailsFormat2.text = it.Name.filetype
                binding.videoDetailsNestedScrollView.tvDetailsDuration2.text =
                    (it.details?.format?.duration?.toFloat()
                        ?.div(60))?.toInt().toString() + "min"
                binding.videoDetailsNestedScrollView.tvDetailsSize2.text =
                    it.details?.format?.size?.toLong()?.diskDataFormat
                binding.videoDetailsNestedScrollView.tvDetailsCreate2.text =
                    (it.Atim.toString().substring(0, 10)
                        .toLong() * 1000) dateFormat "yyyy-MM-dd hh:mm"
                it.details?.streams?.forEach { stream ->
                    when (stream?.codec_type) {
                        "video" -> binding.videoDetailsNestedScrollView.tvDetailsVideoformat2
                            .text = "${stream.width ?: "?"}x${stream.height ?: "?"} ${
                            stream.codec_long_name?.split("/")?.first() ?: "unknown"
                        }"
                        "audio" -> binding.videoDetailsNestedScrollView.tvDetailsAudioformat2
                            .text = stream.codec_long_name?.split(" ")?.first()
                        else -> {}
                    }
                }
            }
        }


        binding.btnVideoDetailsDetermine.setOnClickListener {
            mainViewModel.repository.getDlnaDevices(this, mainViewModel, mediaViewModel)
        }

        val adapter = DeviceAdapter(listOf())
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                if (!isSpinnerFirstStart){
                    val device = parent?.adapter?.getItem(position) as DlnaDevice
                    mainViewModel.repository.setScreenProjection(this@VideoDetailsFragment,
                        "${video.Path}/${video.Name}",
                        device.Id,
                        mainViewModel)
                    mediaViewModel.currentDevice = device
                }
                isSpinnerFirstStart = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        mediaViewModel.dlnaDevices.observe(this) {
            adapter.devices = it
            adapter.notifyDataSetChanged()
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()

        exoPlayer = ExoPlayer.Builder(videoView.context).build()
        onPreviewListener.invoke(exoPlayer)
        videoView.player = exoPlayer
//        exoPlayer.prepare()
//        exoPlayer.play()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer.stop()
        exoPlayer.release()
        _binding = null
    }


    fun setOnPreviewListener(func: (ExoPlayer) -> Unit) {
        onPreviewListener = func
    }

    private class DeviceAdapter(var devices: List<DlnaDevice>) : BaseAdapter() {

        override fun getCount() = devices.size

        override fun getItem(position: Int) = devices[position]

        override fun getItemId(position: Int) = position.toLong() + 1

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//            return convertView
//                ?: DlnaDevicesSpinnerBinding.inflate(LayoutInflater.from(parent?.context),
//                    parent, false).root
            val binding = if (convertView == null) {
                DlnaDevicesSpinnerBinding.inflate(LayoutInflater.from(parent?.context),
                    parent,
                    false)
            } else {
                DlnaDevicesSpinnerBinding.bind(convertView)
            }

            binding.tvDlnaName.text = devices[position].Name
            binding.tvDlnaHost.text = devices[position].Host

            return binding.root
        }


    }

}




