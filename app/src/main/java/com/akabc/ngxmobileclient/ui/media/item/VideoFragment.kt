package com.akabc.ngxmobileclient.ui.media.item

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
        val adapter = VideoAdapter(getString(R.string.video_thumbnail_url),
            mediaViewModel,
            mainViewModel.repository)

        adapter.itemWidth =
            (resources.displayMetrics.widthPixels / 2 - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                12F, resources.displayMetrics)).toInt()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class VideoAdapter(
        val url: String,
        private val mediaViewModel: MediaViewModel,
        val repository: Repository,
    ) :
        RecyclerView.Adapter<VideoAdapter.ViewHolder>() {
        var dataSet: List<Video> = listOf()
        var itemWidth = 504
        val screenAspectRatio = 9.0 / 16.0

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvVideoTitle: TextView
            val tvVideoDate: TextView
            val tvVideoSize: TextView
            val imVideo: ImageView

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

            holder.imVideo.layoutParams.height = (itemWidth * screenAspectRatio).toInt()

            val lastUrl = "?Src=${video.Path.replace("/", "%2F")}/${
                video.Name.replace(".", "%2E")
            }&Tick=-1&Width=${itemWidth}&Height=${(itemWidth * screenAspectRatio).toInt()}&Authorization="
            if (video.cover != null) {
                holder.imVideo.setImageBitmap(video.cover)
            } else {
                repository.getVideoThumbnail(url + lastUrl, position, mediaViewModel)
            }
        }

        override fun getItemCount() = dataSet.size
    }

}