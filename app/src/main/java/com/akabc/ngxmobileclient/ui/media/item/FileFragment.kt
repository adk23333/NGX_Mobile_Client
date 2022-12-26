package com.akabc.ngxmobileclient.ui.media.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akabc.ngxmobileclient.databinding.FragmentFileBinding


class FileFragment : Fragment() {
    private var _binding: FragmentFileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding =  FragmentFileBinding.inflate(inflater, container, false)
        //TODO

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}