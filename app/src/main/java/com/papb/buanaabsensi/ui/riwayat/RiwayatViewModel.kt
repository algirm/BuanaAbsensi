package com.papb.buanaabsensi.ui.riwayat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.papb.buanaabsensi.domain.PresensiRepo
import com.papb.buanaabsensi.ui.base.BaseViewModel
import com.papb.buanaabsensi.util.DispatcherProvider
import com.papb.buanaabsensi.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RiwayatViewModel @Inject constructor(
    private val presensiRepo: PresensiRepo,
    private val dispatcher: DispatcherProvider
): BaseViewModel() {

    private val _state = MutableLiveData<RiwayatViewState>()
    val state: LiveData<RiwayatViewState> = _state

    init {
        getRiwayatPresensi()
    }

    fun getRiwayatPresensi() = viewModelScope.launch {
        _state.value = RiwayatViewState.Loading
        try {
            withContext(dispatcher.io) {
                presensiRepo.getRiwayatPresensi().collect { result ->
                    Timber.d("result is $result")
                    when(result) {
                        is Resource.Failure -> {
                            _state.postValue(RiwayatViewState.Error(result.throwable?.message))
                            handleError(result.throwable?.message)
                        }
                        is Resource.Success -> {
                            _state.postValue(RiwayatViewState.Success(result.data))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            _state.value = RiwayatViewState.Error(e.message)
            handleError(e.message)
        }
    }

}