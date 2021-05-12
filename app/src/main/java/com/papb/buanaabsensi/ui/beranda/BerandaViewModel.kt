package com.papb.buanaabsensi.ui.beranda

import androidx.lifecycle.viewModelScope
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
class BerandaViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val dispatcher: DispatcherProvider
) : BaseViewModel() {

    private val _state: MutableStateFlow<BerandaState> = MutableStateFlow(BerandaState.Loading)
    val state: StateFlow<BerandaState> = _state

    init {
        getUserData()
    }

    private fun getUserData() = viewModelScope.launch {
        _state.value = BerandaState.Loading
        try {
            withContext(dispatcher.io) {
                authRepo.getUserData().collect { result ->
                    Timber.d("result is $result")
                    when(result) {
                        is Resource.Failure -> {
                            _state.value = BerandaState.Error(result.throwable?.message)
                            handleError(result.throwable?.message)
                        }
                        is Resource.Success -> { _state.value = BerandaState.Success(result.data) }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            _state.value = BerandaState.Error(e.message)
            handleError(e.message)
        }
    }

}