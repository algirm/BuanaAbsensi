package com.papb.buanaabsensi.ui.register

import androidx.lifecycle.viewModelScope
import com.papb.buanaabsensi.data.model.Pegawai
import com.papb.buanaabsensi.domain.AuthRepo
import com.papb.buanaabsensi.ui.base.BaseViewModel
import com.papb.buanaabsensi.util.DispatcherProvider
import com.papb.buanaabsensi.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val dispatcher: DispatcherProvider
) : BaseViewModel() {

    private val _state = MutableStateFlow(RegisterViewState())
    val state: StateFlow<RegisterViewState> = _state

    fun daftarAkun(pegawai: Pegawai) = viewModelScope.launch {
        updateState(state.value.copy(loadingDaftar = true))
        withContext(dispatcher.io) {
            try {
                authRepo.daftarAkun(pegawai).collect { result ->
                    Timber.d("result is $result")
                    when(result) {
                        is Resource.Success -> {
                            updateState(state.value.copy(
                                isSuccess = true,
                                loadingDaftar = false
                            ))
                        }
                        is Resource.Failure -> {
                            updateState(state.value.copy(
                                isSuccess = false,
                                loadingDaftar = false
                            ))
                            handleError(result.throwable?.message)
                        }
                    }
                }
            } catch (t: Throwable) {
                Timber.e(t)
                updateState(state.value.copy(
                    isSuccess = false,
                    loadingDaftar = false
                ))
                handleError(t.message)
            }
        }
    }

    fun checkNipAvailable(nip: String) = viewModelScope.launch {
        updateState(state.value.copy(loadingNip = true))
        withContext(dispatcher.io) {
            try {
                authRepo.checkNipAvail(nip).collect { result ->
                    Timber.d("result is $result")
                    if (result is Resource.Success) {
                        updateState(state.value.copy(
                            nipAvail = result.data,
                            loadingNip = false
                        ))
                    }
                }
            } catch (t: Throwable) {
                Timber.e(t)
                updateState(state.value.copy(
                    nipAvail = null,
                    loadingNip = false
                ))
                handleError(t.message)
            }
        }
    }

    fun updateState(viewState: RegisterViewState) = viewModelScope.launch {
        _state.value = viewState
    }

}