package com.papb.buanaabsensi.data.model

import com.google.firebase.Timestamp

data class DaftarPresensi(
    var tanggal: Timestamp? = null,
    @JvmField
    var isActive: Boolean? = null
)
