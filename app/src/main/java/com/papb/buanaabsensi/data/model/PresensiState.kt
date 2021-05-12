package com.papb.buanaabsensi.data.model

sealed class PresensiState {
    object NoPresensi : PresensiState()
    data class Available(
        val daftarPresensi: DaftarPresensi,
        val presensi: Presensi,
        val idPresensi: String
    ) : PresensiState()
    object Error : PresensiState()
}
