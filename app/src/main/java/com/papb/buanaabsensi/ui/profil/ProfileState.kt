package com.papb.buanaabsensi.ui.profil

import com.papb.buanaabsensi.data.model.Pegawai

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val data: Pegawai) : ProfileState()
    data class Error(val errorMessage: String?) : ProfileState()
}
