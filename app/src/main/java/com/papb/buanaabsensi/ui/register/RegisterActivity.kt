package com.papb.buanaabsensi.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.papb.buanaabsensi.R
import com.papb.buanaabsensi.data.model.Pegawai
import com.papb.buanaabsensi.databinding.ActivityRegisterBinding
import com.papb.buanaabsensi.util.Constants.Companion.RESULT_DAFTAR_SUKSES
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var lastState: RegisterViewState
    private var checkJob: Job? = null

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        lastState = viewModel.state.value
        bindUi()
        handleError()
    }

    private fun initView() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        binding.textNipAvail.text = ""
        binding.btDaftar.setOnClickListener {
            viewModel.updateState(
                lastState.copy(
                    loadingDaftar = true
                )
            )
            daftarAkun()
        }
        binding.etNip.addTextChangedListener {
            checkJob?.cancel()
            if (it.toString().toCharArray().size >= 6) {
                viewModel.updateState(
                    lastState.copy(
                        loadingNip = true
                    )
                )
                checkJob = lifecycleScope.launchWhenCreated {
                    delay(1000L)
                    viewModel.checkNipAvailable(it.toString())
                }
            } else {
                viewModel.updateState(
                    lastState.copy(
                        loadingNip = false,
                        nipAvail = null
                    )
                )
            }
        }
    }

    private fun bindUi() = lifecycleScope.launchWhenCreated {
        viewModel.state.collect { state ->
            Timber.d("last state:$lastState\ncurrent State: $state")
            lastState = state
            showLoadingDaftar(state.loadingDaftar)
            showNipAvailState(state)

            if (state.isSuccess == true) {
                Toast.makeText(
                    this@RegisterActivity,
                    getString(R.string.berhasil_daftar),
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent().putExtra("email", binding.etEmail.text.toString())
                setResult(RESULT_DAFTAR_SUKSES, intent)
                firebaseAuth.signOut()
                finish()
            }

        }
    }

    private fun showNipAvailState(state: RegisterViewState) {
        if (state.loadingNip) {
            binding.nipProgressBar.show()
            binding.textNipAvail.visibility = View.GONE
        } else {
            binding.nipProgressBar.hide()
            binding.textNipAvail.visibility = View.VISIBLE
        }
        when (state.nipAvail) {
            true -> binding.textNipAvail.text = ""
            false -> binding.textNipAvail.text = getString(R.string.nip_belum_terdaftar)
            null -> binding.textNipAvail.text = ""
        }
    }

    private fun showLoadingDaftar(loading: Boolean) {
        if (loading) {
            binding.progressBar.show()
        } else {
            binding.progressBar.hide()
        }
    }

    private fun handleError() = lifecycleScope.launchWhenCreated {
        viewModel.errorEvent.collect { errorMessage ->
            Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun daftarAkun() {
        if (!isValidInput() || !isPasswordCorrect() || lastState.nipAvail != true) {
            viewModel.updateState(
                lastState.copy(
                    validInput = false,
                    loadingDaftar = false
                )
            )
            viewModel.handleError(getString(R.string.harap_isi_semua_dengan_benar))
            return
        }

        try {
            firebaseAuth.createUserWithEmailAndPassword(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            ).addOnSuccessListener { result ->
                viewModel.daftarAkun(
                    Pegawai(
                        result.user!!.uid,
                        binding.etNip.text.toString(),
                        binding.etNamaDepan.text.toString(),
                        binding.etNamaBelakang.text.toString(),
                        binding.etEmail.text.toString(),
                        isActive = true
                    )
                )
            }.addOnFailureListener { exception ->
                viewModel.handleError(exception.message)
                viewModel.updateState(lastState.copy(loadingDaftar = false))
            }
        } catch (e: Exception) {
            viewModel.handleError(e.message)
            viewModel.updateState(lastState.copy(loadingDaftar = false))
        }
    }

    private fun isPasswordCorrect(): Boolean {
        with(binding) {
            return if (etPassword.text.toString() == etConfirmPassword.text.toString()) {
                true
            } else {
                etConfirmPassword.error = getString(R.string.password_tidak_sama)
                false
            }
        }
    }

    private fun isValidInput(): Boolean {
        with(binding) {
            val errorKosong = getString(R.string.harus_diisi)
            when {
                etNip.text.isBlank() -> etNip.error = errorKosong
                etNamaDepan.text.isBlank() -> etNamaDepan.error = errorKosong
                etEmail.text.isBlank() -> etEmail.error = errorKosong
                etPassword.text.isBlank() -> etPassword.error = errorKosong
                etPassword.text.length < 6 -> etPassword.error =
                    getString(R.string.password_minimal)
                etConfirmPassword.text.isBlank() -> etConfirmPassword.error = errorKosong
                else -> return true
            }
            return false
        }
    }

}