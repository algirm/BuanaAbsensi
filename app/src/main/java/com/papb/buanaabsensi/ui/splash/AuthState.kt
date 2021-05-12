package com.papb.buanaabsensi.ui.splash

sealed class AuthState {
    object Loading : AuthState()
    object Init : AuthState()
    object LoggedIn : AuthState()
    object NotLogged : AuthState()
    data class Error(val errorMessage: String?) : AuthState()
}