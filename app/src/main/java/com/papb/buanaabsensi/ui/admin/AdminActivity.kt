package com.papb.buanaabsensi.ui.admin

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.papb.buanaabsensi.R
import com.papb.buanaabsensi.data.model.DaftarPresensi
import com.papb.buanaabsensi.databinding.ActivityAdminBinding
import com.papb.buanaabsensi.ui.dialog.ConfirmDialogFragment
import com.papb.buanaabsensi.ui.dialog.ConfirmDialogListener
import com.papb.buanaabsensi.ui.dialog.TambahNipDialogFragment
import com.papb.buanaabsensi.ui.login.LoginActivity
import com.papb.buanaabsensi.util.Constants.Companion.BUKA_PRESENSI_CODE
import com.papb.buanaabsensi.util.Constants.Companion.TAMBAH_NIP_CODE
import com.papb.buanaabsensi.util.Constants.Companion.TUTUP_PRESENSI_CODE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@AndroidEntryPoint
class AdminActivity : AppCompatActivity(), ConfirmDialogListener {

    private lateinit var binding: ActivityAdminBinding
    private val viewModel: AdminViewModel by viewModels()
    private var activePresensi: DaftarPresensi? = null
    private lateinit var confirmDialogFragment: ConfirmDialogFragment
    private lateinit var tambahNipDialogFragment: TambahNipDialogFragment

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding.cardKeluar.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.cardBukaPresensi.setOnClickListener {
            val args = Bundle()
            when {
                activePresensi?.isActive == true -> {
                    args.putString("header", "Tutup Presensi Hari Ini?")
                    args.putInt("eventCode", TUTUP_PRESENSI_CODE)
                    confirmDialogFragment.arguments = args
                    confirmDialogFragment.show(supportFragmentManager, "confirmDialogTutupPresensi")
                }
                activePresensi?.isActive == false || activePresensi == null -> {
                    args.putString("header", "Buka Presensi Hari Ini?")
                    args.putInt("eventCode", BUKA_PRESENSI_CODE)
                    confirmDialogFragment.arguments = args
                    confirmDialogFragment.show(supportFragmentManager, "confirmDialogBukaPresensi")
                }
            }
        }
        binding.cardTambahNip.setOnClickListener {
            val args = Bundle()
            args.putInt("eventCode", TAMBAH_NIP_CODE)
            tambahNipDialogFragment.arguments = args
            tambahNipDialogFragment.show(supportFragmentManager, "tambahDialogFragment")
        }
        viewModel.getPresensiHariIni()
        lifecycleScope.launchWhenCreated {
            viewModel.activePresensi.collect {
                Timber.d("statePresensi: $it")
                activePresensi = it
                when (it?.isActive) {
                    true -> {
                        binding.textAktif.visibility = View.VISIBLE
                        binding.textCard.text = getString(R.string.tutup_prsnsi_hariini)
                    }
                    else -> {
                        binding.textAktif.visibility = View.INVISIBLE
                        binding.textCard.text = getString(R.string.buka_prsnsi_hariini)
                    }
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.tambahEvent.collect {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "$it Telah Ditambahkan",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
        tambahNipDialogFragment = TambahNipDialogFragment.newInstance()
        confirmDialogFragment = ConfirmDialogFragment.newInstance()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, eventCode: Int?, bundle: Bundle?) {
        when (eventCode) {
            TAMBAH_NIP_CODE -> viewModel.tambahNip(bundle?.getString("nip")!!)
            BUKA_PRESENSI_CODE -> viewModel.bukaPresensi()
            TUTUP_PRESENSI_CODE -> viewModel.tutupPresensiHariIni()
            else -> Toast.makeText(this, "Something Wrong", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }
}