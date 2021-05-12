package com.papb.buanaabsensi.ui.profil

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.papb.buanaabsensi.R
import com.papb.buanaabsensi.data.model.Pegawai
import com.papb.buanaabsensi.databinding.FragmentProfilBinding
import com.papb.buanaabsensi.ui.base.BaseFragment
import com.papb.buanaabsensi.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ProfilFragment : BaseFragment(R.layout.fragment_profil) {

    private val binding by viewBinding(FragmentProfilBinding::bind)
    private val viewModel: ProfilViewModel by activityViewModels()
    private var pegawai: Pegawai? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pegawai = arguments?.getSerializable("pegawai") as Pegawai?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (pegawai == null) {
            viewModel.getUserData()
        } else {
            pegawai?.let { bindUi(it) }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.state.collect { state ->
                when(state) {
                    is ProfileState.Error -> {}
                    ProfileState.Loading -> {}
                    is ProfileState.Success -> {
                        bindUi(state.data)
                        pegawai = state.data
                    }
                }
            }
        }
        lifecycleScope.launchWhenCreated { viewModel.errorEvent.collect { toastShort(it) } }
    }

    private fun bindUi(data: Pegawai) {
        with(binding) {
            "${data.namaDepan} ${data.namaBelakang}".also { textView2.text = it }
            data.createdAt?.let { timestamp ->
                val tahun =
                    SimpleDateFormat("yyyy", Locale.ROOT).format(timestamp.toDate())
                textView4.text = tahun
            }
            textView6.text = if (data.isActive == true) {
                getString(R.string.aktif)
            } else {
                getString(R.string.tidak_aktif)
            }
            tvNip.text = data.nip
            tvEmail.text = data.email
            textView8.text = data.email
            textView10.text = data.nip
        }
    }

}