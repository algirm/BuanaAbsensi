package com.papb.buanaabsensi.ui.profil

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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfilViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val dispatcher: DispatcherProvider
) : BaseViewModel() {

    private val _state: MutableStateFlow<ProfileState> = MutableStateFlow(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state

    fun getUserData() = viewModelScope.launch {
        _state.value = ProfileState.Loading
        try {
            authRepo.getUserData().collect { result ->
                Timber.d("result is $result")
                when(result) {
                    is Resource.Failure -> {
                        _state.value = ProfileState.Error(result.throwable?.message)
                        handleError(result.throwable?.message)
                    }
                    is Resource.Success -> { _state.value = ProfileState.Success(result.data) }
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            _state.value = ProfileState.Error(e.message)
            handleError(e.message)
        }
    }

}