package com.akabc.ngxmobileclient.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.R
import com.akabc.ngxmobileclient.databinding.FragmentMediaBinding
import com.akabc.ngxmobileclient.ui.media.item.FileFragment
import com.akabc.ngxmobileclient.ui.media.item.MusicFragment
import com.akabc.ngxmobileclient.ui.media.item.VideoFragment
import com.google.android.material.tabs.TabLayoutMediator

class MediaFragment : Fragment() {

    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        val mediaViewModel = ViewModelProvider(this)[MediaViewModel::class.java]

        val viewPager2 = binding.vpMedia
        val mediaTab = binding.mediaTab

        mainViewModel.setFab(R.drawable.ic_search, 56)
        viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 3

            override fun createFragment(position: Int) = when (position) {
                0 -> VideoFragment()
                1 -> MusicFragment()
                else -> FileFragment()
            }
        }

        TabLayoutMediator(mediaTab, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.setText(R.string.media_tab_video)
                1 -> tab.setText(R.string.media_tab_music)
                else -> tab.setText(R.string.media_tab_file)
            }
        }.attach()


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}