package com.papb.buanaabsensi.ui.register

data class RegisterViewState(
    val loadingNip: Boolean = false,
    val loadingDaftar: Boolean = false,
    val nipAvail: Boolean? = null,
    val validInput: Boolean? = null,
    val isSuccess: Boolean? = null
)
