package com.bogleo.musicapp.screens.tracks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bogleo.musicapp.R
import com.bogleo.musicapp.adaptors.recycler.SongListAdapter
import com.bogleo.musicapp.common.Status
import com.bogleo.musicapp.databinding.FragmentTracksBinding
import com.bogleo.musicapp.screens.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "TracksFragment"

@AndroidEntryPoint
class TracksFragment : Fragment() {

    @Inject
    lateinit var songListAdapter: SongListAdapter

    private val mainViewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentTracksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.recyclerTracks) {
            adapter = songListAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL,
                false
            )
        }

        mainViewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when(result.status) {
                is Status.Loading -> setLoadingVisibility(isLoading = true)
                is Status.Success -> {
                    songListAdapter.submitList(result.data)
                    setLoadingVisibility(isLoading = false)
                }
                is Status.Error<*> -> Log.e(TAG, result.status.message.toString())
            }
        }
    }

    private fun setLoadingVisibility(isLoading: Boolean) {
        with(binding) {
            if(isLoading) {
                progressBarTracks.visibility = View.VISIBLE
                recyclerTracks.visibility = View.GONE
            } else {
                progressBarTracks.visibility = View.GONE
                recyclerTracks.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}