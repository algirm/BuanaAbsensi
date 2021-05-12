package com.papb.buanaabsensi.ui.beranda

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.papb.buanaabsensi.R
import com.papb.buanaabsensi.data.model.Pegawai
import com.papb.buanaabsensi.ui.base.BaseFragment
import com.papb.buanaabsensi.databinding.FragmentBerandaBinding
import com.papb.buanaabsensi.ui.login.LoginActivity
import com.papb.buanaabsensi.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BerandaFragment : BaseFragment(R.layout.fragment_beranda) {

    private val binding by viewBinding(FragmentBerandaBinding::bind)
    private val viewModel: BerandaViewModel by activityViewModels()
    private var pegawai: Pegawai? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        lifecycleScope.launchWhenCreated {
            viewModel.state.collect { state ->
                showLoading(state is BerandaState.Loading)
                when(state) {
                    is BerandaState.Error -> {}
                    BerandaState.Loading -> {}
                    is BerandaState.Success -> {
                        bindUi(state.data)
                        pegawai = state.data
                    }
                }
            }
        }
        lifecycleScope.launchWhenCreated { viewModel.errorEvent.collect { toastShort(it) } }
    }

    private fun showLoading(bool: Boolean) {
        if (bool) {
            binding.progressBar.show()
            binding.tvWelcome.visibility = View.INVISIBLE
            binding.textFullname.visibility = View.INVISIBLE
        } else {
            binding.progressBar.hide()
            binding.tvWelcome.visibility = View.VISIBLE
            binding.textFullname.visibility = View.VISIBLE
        }
    }

    private fun bindUi(data: Pegawai) {
        with(binding) {
            "${data.namaDepan} ${data.namaBelakang}".also { textFullname.text = it }
        }
    }

    private fun setupNavigation() {
        with(binding) {
            cardPresensi.setOnClickListener {
                findNavController().navigate(
                    BerandaFragmentDirections.actionNavBerandaToNavPresensi()
                )
            }
            cardProfil.setOnClickListener {
                findNavController().navigate(
                    BerandaFragmentDirections.actionNavBerandaToNavProfil(pegawai)
                )
            }
            cardRiwayat.setOnClickListener {
                findNavController().navigate(
                    BerandaFragmentDirections.actionNavBerandaToNavRiwayat()
                )
            }
            cardLogout.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                requireActivity().startActivity(Intent(requireActivity(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }
    }

}