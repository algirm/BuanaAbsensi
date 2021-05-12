package com.papb.buanaabsensi.util

class Constants {
    companion object {
        const val RESULT_DAFTAR_SUKSES = 0
        const val BUKA_PRESENSI_CODE = 7
        const val TUTUP_PRESENSI_CODE = 8
        const val TAMBAH_NIP_CODE = 9
        const val REQUEST_CODE_LOCATION_PERMISSION = 10
        const val LOCATION_OFFICE = "-6.182326175405704,106.67773467064735"

        // Tracking Options
        const val LOCATION_UPDATE_INTERVAL = 5000L
        const val FASTEST_LOCATION_UPDATE_INTERVAL = 2000L

        // Map Options
        const val MAP_ZOOM = 15f

        // Status Presensi
        const val ALPHA = -1
        const val BELUM_PRESENSI = 0
        const val SELESAI = 1
    }
}