package com.akabc.ngxmobileclient.ui.media.item

import android.app.Activity
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akabc.ngxmobileclient.MainViewModel
import com.akabc.ngxmobileclient.R
import com.akabc.ngxmobileclient.data.Video
import com.akabc.ngxmobileclient.databinding.FragmentVideoBinding
import com.akabc.ngxmobileclient.net.Repository
import com.akabc.ngxmobileclient.net.dateFormat
import com.akabc.ngxmobileclient.net.memDataFormat
import com.akabc.ngxmobileclient.ui.media.MediaViewModel
import com.android.volley.toolbox.NetworkImageView


class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!
    private lateinit var mediaViewModel: MediaViewModel
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        mediaViewModel = ViewModelProvider(this)[MediaViewModel::class.java]

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.repository.getVideos(getString(R.string.media_url), mainViewModel, mediaViewModel)
        val recyclerView = binding.rvVideo
        val adapter = VideoAdapter(getString(R.string.video_thumbnail_url),
            requireActivity(),
            mainViewModel.repository)


        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(recyclerView.context, 2)
        adapter.itemWidth =
            (resources.displayMetrics.widthPixels / 2 - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                12F, resources.displayMetrics)).toInt()
        mediaViewModel.videos.observe(viewLifecycleOwner) {
            adapter.dataSet = it
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class VideoAdapter(val url: String, val activity: Activity, val repository: Repository) :
        RecyclerView.Adapter<VideoAdapter.ViewHolder>() {
        var dataSet: List<Video> = listOf()
        var itemWidth = 504

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvVideoTitle: TextView
            val tvVideoDate: TextView
            val tvVideoSize: TextView
            val imVideo: NetworkImageView

            init {
                tvVideoTitle = view.findViewById(R.id.tv_video_title)
                tvVideoDate = view.findViewById(R.id.tv_video_date)
                tvVideoSize = view.findViewById(R.id.tv_video_size)
                imVideo = view.findViewById(R.id.im_video)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_video_item, parent, false)
            view.layoutParams.width = itemWidth
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val video = dataSet[position]
            holder.tvVideoTitle.text = video.Name
            holder.tvVideoDate.text = (video.Atim.toString().substring(0, 10)
                .toLong() * 1000) dateFormat "yyyy-MM-dd hh:mm"
            holder.tvVideoSize.text = video.Size.memDataFormat
            val lastUrl = "?Src=${video.Path.replace("/", "%2F")}/${
                video.Name.replace(".", "%2E")
            }&Tick=-1&Width=${itemWidth}&Height=${holder.imVideo.layoutParams.height}&Authorization="
            repository.getVideoThumbnail(url + lastUrl, holder.imVideo)
            //9784B038-E04B-1174-645F-DAE2409817E2
        }

        override fun getItemCount() = dataSet.size
    }

}