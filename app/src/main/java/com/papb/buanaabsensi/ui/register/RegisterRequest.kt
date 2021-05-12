package com.papb.buanaabsensi.ui.register

data class RegisterRequest(
    val nip: String,
    val namaDepan: String,
    val namaBelakang: String,
    val email: String,
    val uid: String
)
