package com.papb.buanaabsensi.domain

import com.papb.buanaabsensi.data.model.DaftarPresensi
import com.papb.buanaabsensi.data.model.Presensi
import com.papb.buanaabsensi.data.model.PresensiState
import com.papb.buanaabsensi.util.Resource
import kotlinx.coroutines.flow.Flow

interface PresensiRepo {
    suspend fun bukaPresensi(): Flow<DaftarPresensi>
    suspend fun getPresensiHariIni(): Flow<DaftarPresensi?>
    suspend fun tutupPresensiHariIni(): Flow<DaftarPresensi>
    suspend fun getPresensiPegawai(): Flow<PresensiState>
    suspend fun presensi(presensi: Presensi, id: String): Flow<Boolean>
    suspend fun getRiwayatPresensi(): Flow<Resource<List<Presensi>>>
}