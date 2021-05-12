package com.papb.buanaabsensi.data.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class Pegawai(
    var uid: String? = null,
    var nip: String? = null,
    var namaDepan: String? = null,
    var namaBelakang: String? = null,
    var email: String? = null,
    var createdAt: Timestamp? = null,
    @JvmField
    var isActive: Boolean? = null
) : Serializable
