package com.papb.buanaabsensi.data.model

import com.google.firebase.Timestamp

data class Presensi(
    var uid: String? = null,
    var tanggal: Timestamp? = null,
    var tanggalPresensi: Timestamp? = null,
    var statusPresensi: Int? = null
)
