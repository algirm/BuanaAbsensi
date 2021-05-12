package com.papb.buanaabsensi.ui.splash

import androidx.lifecycle.viewModelScope
import com.papb.buanaabsensi.domain.AuthRepo
import com.papb.buanaabsensi.ui.base.BaseViewModel
import com.papb.buanaabsensi.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepo: AuthRepo
) : BaseViewModel() {

    private val _authState: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Init)
    val authState: StateFlow<AuthState> = _authState

    fun checkLogin() = viewModelScope.launch {
        _authState.value = AuthState.Loading
        try {
            authRepo.checkLogin().collect { result ->
                Timber.d("result is $result")
                if (result is Resource.Success)
                    _authState.value = if (result.data) AuthState.LoggedIn else AuthState.NotLogged
            }
        } catch (e: Exception) {
            Timber.e(e)
            _authState.value = AuthState.Error(e.message)
            handleError(e.message)
        }
    }

}