package com.akabc.ngxmobileclient.ui.media.item

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.R
import com.akabc.ngxmobileclient.data.Video
import com.akabc.ngxmobileclient.databinding.FragmentVideoBinding
import com.akabc.ngxmobileclient.databinding.FragmentVideoItemBinding
import com.akabc.ngxmobileclient.net.Repository
import com.akabc.ngxmobileclient.net.dateFormat
import com.akabc.ngxmobileclient.net.memDataFormat
import com.akabc.ngxmobileclient.ui.media.MediaViewModel
import kotlin.math.max


class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!
    private val mediaViewModel: MediaViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.repository.getVideos(getString(R.string.media_url),
            mainViewModel,
            mediaViewModel)
        val recyclerView = binding.rvVideo
        val adapter = VideoAdapter(getString(R.string.video_thumbnail_url), mediaViewModel,
            mainViewModel.repository).apply {
            itemWidth = (resources.displayMetrics.widthPixels / 2
                    - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                12F,
                resources.displayMetrics)).toInt()
            preloadItemCont = 2
            setOnPreload {

            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(recyclerView.context, 2)
        mediaViewModel.videos.observe(viewLifecycleOwner) {
            adapter.dataSet = it
            if (recyclerView.isComputingLayout) {
                recyclerView.post {
                    Runnable { adapter.notifyDataSetChanged() }
                }
            } else {
                adapter.notifyDataSetChanged()
            }
        }

        adapter.setOnItemClickListener { video, itemView, position ->
            val videoDetailsFragment = VideoDetailsFragment()
            videoDetailsFragment.position = position
            videoDetailsFragment.setOnPreviewListener { exoPlayer ->
                mainViewModel.repository.getVideoDetails(
                    "${video.Path}/${video.Name}", this, position, mainViewModel, mediaViewModel)
                mainViewModel.repository.getVideo(exoPlayer, video, this)
            }
            videoDetailsFragment.show(parentFragmentManager, "vpapFragment")

        }
        adapter.setOnButtonClickListener { video, view ->
            if (mediaViewModel.currentDevice != null) {
                mainViewModel.repository.setScreenProjection(this,
                    "${video.Path}/${video.Name}",
                    mediaViewModel.currentDevice!!.Id,
                    mainViewModel)
                Toast.makeText(requireActivity(), "已推送！", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(), "没有最近投屏设备记录.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /**
     *
     *
     * 控制列表项
     *
     *
     */

    private class VideoAdapter(
        val url: String,
        private val mediaViewModel: MediaViewModel,
        val repository: Repository,
    ) :
        RecyclerView.Adapter<VideoAdapter.ViewHolder>() {
        var dataSet: List<Video> = listOf()
        var itemWidth = 504
        val screenAspectRatio = 9.0 / 16.0
        private var onPreload: (() -> Unit)? = null
        var preloadItemCont = 0
        private var scrollState = SCROLL_STATE_IDLE
        private lateinit var onItemViewClick: (video: Video, itemView: View, position: Int) -> Unit

        fun setOnPreload(func: () -> Unit) {
            onPreload = func
        }

        fun setOnItemClickListener(func: (video: Video, itemView: View, position: Int) -> Unit) {
            onItemViewClick = func
        }

        private lateinit var onButtonClickListener: (Video, View) -> Unit
        fun setOnButtonClickListener(func: (Video, View) -> Unit) {
            onButtonClickListener = func
        }

        class ViewHolder(binding: FragmentVideoItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            val cvVideo: CardView
            val tvVideoTitle: TextView
            val tvVideoDate: TextView
            val tvVideoSize: TextView
            val imVideo: ImageView
            val btn: Button

            init {
                cvVideo = binding.cvVideo
                tvVideoTitle = binding.tvVideoTitle
                tvVideoDate = binding.tvVideoDate
                tvVideoSize = binding.tvVideoSize
                imVideo = binding.imVideo
                btn = binding.btnVideoPlay
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                FragmentVideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            binding.root.layoutParams.width = itemWidth
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val video = dataSet[position]
            holder.tvVideoTitle.text = video.Name
            holder.tvVideoDate.text = (video.Atim.toString().substring(0, 10)
                .toLong() * 1000) dateFormat "yyyy-MM-dd hh:mm"
            holder.tvVideoSize.text = video.Size.memDataFormat

            holder.imVideo.layoutParams.height = (itemWidth * screenAspectRatio).toInt()

            val lastUrl = "?Src=${video.Path.replace("/", "%2F")}/${
                video.Name.replace(".", "%2E")
            }&Tick=-1&Width=${itemWidth}&Height=${(itemWidth * screenAspectRatio).toInt()}&Authorization="
            if (video.cover != null) {
                holder.imVideo.setImageBitmap(video.cover)
            } else {
                repository.getVideoThumbnail(url + lastUrl, position, mediaViewModel)
            }

            holder.cvVideo.setOnClickListener {
                onItemViewClick.invoke(video, it, position)
            }
            holder.btn.setOnClickListener {
                onButtonClickListener.invoke(video, it)
            }

            checkPreload(position)
        }

        override fun getItemCount() = dataSet.size

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    scrollState = newState
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })
        }

        private fun checkPreload(position: Int) {
            if (onPreload != null
                && position == max(itemCount - 1 - preloadItemCont, 0)
                && scrollState != SCROLL_STATE_IDLE
            ) {
                onPreload?.invoke()
            }
        }
    }

}