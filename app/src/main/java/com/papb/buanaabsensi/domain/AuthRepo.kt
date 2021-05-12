package com.papb.buanaabsensi.domain

import com.papb.buanaabsensi.data.model.Pegawai
import com.papb.buanaabsensi.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepo {

    suspend fun checkNipAvail(nip: String): Flow<Resource<Boolean>>

    suspend fun daftarAkun(pegawai: Pegawai): Flow<Resource<Boolean>>

    suspend fun checkLogin(): Flow<Resource<Boolean>>
    suspend fun getUserData(): Flow<Resource<Pegawai>>
    suspend fun tambahNip(nip: String): Flow<String>
}