package com.papb.buanaabsensi.ui.beranda

import com.papb.buanaabsensi.data.model.Pegawai

sealed class BerandaState {
    object Loading : BerandaState()
    data class Success(val data: Pegawai) : BerandaState()
    data class Error(val errorMessage: String?) : BerandaState()
}
