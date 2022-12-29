package com.akabc.ngxmobileclient.ui.media.item

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.akabc.ngxmobileclient.databinding.FragmentVpapBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class VideoPreviewAndProjectionFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentVpapBinding? = null
    private val binding get() = _binding!!
    private lateinit var onPreviewListener: (ExoPlayer, Fragment) -> Unit
    private lateinit var videoView: StyledPlayerView
    private lateinit var exoPlayer: ExoPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVpapBinding.inflate(inflater, container, false)

        videoView = binding.videoView


        return binding.root
    }

    override fun onResume() {
        super.onResume()

        exoPlayer = ExoPlayer.Builder(videoView.context).build()
        onPreviewListener.invoke(exoPlayer, this)
        videoView.player = exoPlayer
        exoPlayer.prepare()
        exoPlayer.play()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer.stop()
        exoPlayer.release()
        _binding = null
    }


    fun setOnPreviewListener(func: (ExoPlayer, Fragment) -> Unit) {
        onPreviewListener = func
    }

}