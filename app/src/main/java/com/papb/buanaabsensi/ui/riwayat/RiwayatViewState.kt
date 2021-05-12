package com.papb.buanaabsensi.ui.riwayat

import com.papb.buanaabsensi.data.model.Presensi

sealed class RiwayatViewState {
    object Loading : RiwayatViewState()
    data class Error(val errorMessage: String?) : RiwayatViewState()
    data class Success(val data: List<Presensi>) : RiwayatViewState()
}
