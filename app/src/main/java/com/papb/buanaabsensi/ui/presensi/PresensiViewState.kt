package com.papb.buanaabsensi.ui.presensi

import com.papb.buanaabsensi.data.model.DaftarPresensi
import com.papb.buanaabsensi.data.model.Presensi
import com.papb.buanaabsensi.data.model.PresensiState

data class PresensiViewState(
    val isLoadingPresensi: Boolean? = null,
    val isLoadingText: Boolean = false,
    val isSuccess: Boolean? = null,
    val enableButton: Boolean = false,
    val atOffice: Boolean = false,
    val presensiState: PresensiState? = null
)
