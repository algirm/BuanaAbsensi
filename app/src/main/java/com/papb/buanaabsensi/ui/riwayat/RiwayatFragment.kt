package com.papb.buanaabsensi.ui.riwayat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.papb.buanaabsensi.R
import com.papb.buanaabsensi.databinding.FragmentRiwayatBinding
import com.papb.buanaabsensi.ui.base.BaseFragment
import com.papb.buanaabsensi.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@AndroidEntryPoint
class RiwayatFragment : BaseFragment(R.layout.fragment_riwayat) {

    private val binding by viewBinding(FragmentRiwayatBinding::bind)
    private val viewModel: RiwayatViewModel by activityViewModels()
    private lateinit var riwayatAdapter: RiwayatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rcRiwayat.apply {
            riwayatAdapter = RiwayatAdapter()
            adapter = riwayatAdapter
            layoutManager = LinearLayoutManager(context)
        }
        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.getRiwayatPresensi() }
        lifecycleScope.launchWhenCreated { viewModel.errorEvent.collect { toastShort(it) } }
        viewModel.state.observe(viewLifecycleOwner, { state ->
            if (state !is RiwayatViewState.Loading)
                binding.swipeRefreshLayout.isRefreshing = false
            when(state) {
                is RiwayatViewState.Error -> Timber.e(state.errorMessage)
                RiwayatViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
                is RiwayatViewState.Success -> {
                    riwayatAdapter.listPresensi = state.data
                }
            }
        })
    }

}