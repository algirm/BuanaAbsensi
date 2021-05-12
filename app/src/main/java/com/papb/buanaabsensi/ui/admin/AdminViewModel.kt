package com.papb.buanaabsensi.ui.admin

import androidx.lifecycle.viewModelScope
import com.papb.buanaabsensi.data.model.DaftarPresensi
import com.papb.buanaabsensi.domain.AuthRepo
import com.papb.buanaabsensi.domain.PresensiRepo
import com.papb.buanaabsensi.ui.base.BaseViewModel
import com.papb.buanaabsensi.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val presensiRepo: PresensiRepo,
    private val authRepo: AuthRepo,
    private val dispatcher: DispatcherProvider,
) : BaseViewModel() {

    val activePresensi: MutableStateFlow<DaftarPresensi?> = MutableStateFlow(null)
    private val _tambahEvent = Channel<String?>()
    val tambahEvent = _tambahEvent.receiveAsFlow()

    fun tambahNip(nip: String) = viewModelScope.launch {
        try {
            withContext(dispatcher.io) {
                authRepo.tambahNip(nip).collect {
                    _tambahEvent.send(it)
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            handleError(e.message)
        }
    }

    fun tutupPresensiHariIni() = viewModelScope.launch {
        try {
            withContext(dispatcher.io) {
                presensiRepo.tutupPresensiHariIni().collect {
                    Timber.d("daftar presensi : $it")
                    activePresensi.value = it
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            activePresensi.value = null
        }
    }

    fun getPresensiHariIni() = viewModelScope.launch {
        try {
            withContext(dispatcher.io) {
                presensiRepo.getPresensiHariIni().collect { daftarPresensi ->
                    Timber.d("daftar presensi : $daftarPresensi")
                    activePresensi.value = daftarPresensi
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            activePresensi.value = null
        }
    }

    fun bukaPresensi() = viewModelScope.launch {
        try {
            withContext(dispatcher.io) {
                presensiRepo.bukaPresensi().collect { daftarPresensi ->
                    activePresensi.value = daftarPresensi
                }
            }
        } catch (e: Exception) {
            activePresensi.value = null
        }
    }

}