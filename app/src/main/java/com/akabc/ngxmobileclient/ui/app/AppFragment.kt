package com.akabc.ngxmobileclient.ui.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.akabc.ngxmobileclient.databinding.FragmentAppBinding

class AppFragment: Fragment() {
    private var _binding: FragmentAppBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val appViewModel =
            ViewModelProvider(this).get(AppViewModel::class.java)

        _binding = FragmentAppBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textApp
        appViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}