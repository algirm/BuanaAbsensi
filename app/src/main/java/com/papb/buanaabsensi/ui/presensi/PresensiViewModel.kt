package com.papb.buanaabsensi.ui.presensi

import androidx.lifecycle.viewModelScope
import com.papb.buanaabsensi.data.model.Presensi
import com.papb.buanaabsensi.data.model.PresensiState
import com.papb.buanaabsensi.domain.AuthRepo
import com.papb.buanaabsensi.domain.PresensiRepo
import com.papb.buanaabsensi.ui.base.BaseViewModel
import com.papb.buanaabsensi.util.Constants.Companion.BELUM_PRESENSI
import com.papb.buanaabsensi.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PresensiViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val presensiRepo: PresensiRepo,
    private val dispatcher: DispatcherProvider,
) : BaseViewModel() {

    private val _state: MutableStateFlow<PresensiViewState> = MutableStateFlow(PresensiViewState())
    val state: StateFlow<PresensiViewState> = _state

    fun getPresensiPegawai() = viewModelScope.launch {
//        _state.value = state.value.copy(isLoadingText = true)
        updateState(state.value.copy(isLoadingText = true))
        try {
            withContext(dispatcher.io) {
                presensiRepo.getPresensiPegawai().collect { presensiState ->
                    updateState(
                        state.value.copy(presensiState = presensiState, isLoadingText = false)
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            updateState(
                state.value.copy(isLoadingText = false, presensiState = PresensiState.Error)
            )
        }
    }

    fun presensi(presensi: Presensi, id: String) = viewModelScope.launch {
        updateState(state.value.copy(isLoadingPresensi = true))
        try {
            withContext(dispatcher.io) {
                presensiRepo.presensi(presensi, id).collect {
                    if (it) {
                        updateState(
                            state.value.copy(
                                isLoadingPresensi = false,
                                isSuccess = true,
                                enableButton = false
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            updateState(state.value.copy(isLoadingPresensi = false))
            handleError(e.message)
        }
    }

    private fun shouldEnableButton(): Boolean {
        val state = _state.value
        var should = false
        if (state.atOffice && state.presensiState != null) {
            if (state.presensiState is PresensiState.Available) {
                should = state.presensiState.presensi.statusPresensi == BELUM_PRESENSI &&
                        state.presensiState.daftarPresensi.isActive == true
            }
        }
        return should
    }

    fun updateState(viewViewState: PresensiViewState) = viewModelScope.launch {
        _state.value = viewViewState.copy(enableButton = shouldEnableButton())
    }

}