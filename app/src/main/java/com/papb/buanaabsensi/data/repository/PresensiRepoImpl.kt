package com.papb.buanaabsensi.data.repository

import android.text.format.DateUtils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.papb.buanaabsensi.data.model.DaftarPresensi
import com.papb.buanaabsensi.data.model.Presensi
import com.papb.buanaabsensi.data.model.PresensiState
import com.papb.buanaabsensi.domain.PresensiRepo
import com.papb.buanaabsensi.util.Constants.Companion.ALPHA
import com.papb.buanaabsensi.util.Constants.Companion.BELUM_PRESENSI
import com.papb.buanaabsensi.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PresensiRepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
) : PresensiRepo {

    private val presensiRef = firebaseFirestore.collection("daftar_presensi")
    private val userRef = firebaseFirestore.collection("user")

    override suspend fun getRiwayatPresensi() = flow {
        val result = mutableListOf<Presensi>()
        val uid = firebaseAuth.currentUser!!.uid
        val daftarPresensi =
            presensiRef.orderBy("tanggal", Query.Direction.DESCENDING).get().await()
        for (dp in daftarPresensi) {
            val presensiResult = dp.reference
                .collection("presensi")
                .whereEqualTo("uid", uid)
                .get()
                .await()
                .documents
            if (presensiResult.isNotEmpty()) {
                result.add(presensiResult[0].toObject(Presensi::class.java)!!)
            }
        }
        emit(Resource.Success(result))
    }

    override suspend fun presensi(presensi: Presensi, id: String): Flow<Boolean> = flow {
        presensiRef.document(id).collection("presensi").add(presensi).await()
        emit(true)
    }

    override suspend fun getPresensiPegawai(): Flow<PresensiState> = flow {
        val uid = firebaseAuth.currentUser!!.uid
        val taskResult = presensiRef.orderBy("tanggal", Query.Direction.DESCENDING)
            .get()
            .await()
        if (!taskResult.isEmpty) {
            val daftarPresensi =
                taskResult.documents[0].toObject(DaftarPresensi::class.java)!!
            val tanggalDaftarPresensi = daftarPresensi.tanggal!!
            if (DateUtils.isToday(tanggalDaftarPresensi.toDate().time)) {
                val presensiPegawaiRef = taskResult.documents[0].reference
                    .collection("presensi")
                    .whereEqualTo("uid", uid)
                    .get()
                    .await()
                if (!presensiPegawaiRef.isEmpty) {
                    val presensiPegawai =
                        presensiPegawaiRef.documents[0].toObject(Presensi::class.java)!!
                    emit(
                        PresensiState.Available(
                            daftarPresensi,
                            presensiPegawai,
                            taskResult.documents[0].id
                        )
                    )
                } else {
                    val status = if (daftarPresensi.isActive!!) BELUM_PRESENSI else ALPHA
                    emit(
                        PresensiState.Available(
                            daftarPresensi,
                            Presensi(uid, tanggalDaftarPresensi, statusPresensi = status),
                            taskResult.documents[0].id
                        )
                    )
                }
            } else {
                emit(PresensiState.NoPresensi)
            }
        } else {
            emit(PresensiState.NoPresensi)
        }
    }

    override suspend fun tutupPresensiHariIni(): Flow<DaftarPresensi> = flow {
        val taskResult = presensiRef
            .orderBy("tanggal", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents[0]
            .reference
        taskResult.update("isActive", false).await()

        val userResult = userRef.whereEqualTo("isActive", true).get().await()
        for (user in userResult) {
            val uid = user.getString("uid")!!
            val presensiUser = taskResult.collection("presensi")
                .whereEqualTo("uid", uid)
                .get()
                .await()
            if (presensiUser.documents.isEmpty()) {
                val presensiAlpha = Presensi(
                    uid,
                    taskResult.get().await().getTimestamp("tanggal"),
                    Timestamp.now(),
                    ALPHA
                )
                taskResult.collection("presensi").add(presensiAlpha).await()
            }
        }
        val result = taskResult.get().await()
        emit(result.toObject(DaftarPresensi::class.java)!!)
    }

    override suspend fun getPresensiHariIni(): Flow<DaftarPresensi?> = flow {
        val taskResult = presensiRef
            .orderBy("tanggal", Query.Direction.DESCENDING)
            .get()
            .await()
        if (taskResult.isEmpty) {
            emit(null)
        } else {
            val lastPresensi = taskResult.documents[0].toObject(DaftarPresensi::class.java)!!
            val tanggalLast = lastPresensi.tanggal!!.toDate().time
            if (DateUtils.isToday(tanggalLast)) {
                emit(lastPresensi)
            } else {
                emit(null)
            }
        }
    }

    override suspend fun bukaPresensi(): Flow<DaftarPresensi> = flow {
        val presensiHariIni = presensiRef.orderBy("tanggal", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents[0]
        val tanggalPresensiHariIni = presensiHariIni.getTimestamp("tanggal")!!.toDate().time
        if (DateUtils.isToday(tanggalPresensiHariIni)) {
            presensiHariIni.reference.update("isActive", true).await()
            emit(presensiHariIni.reference.get().await().toObject(DaftarPresensi::class.java)!!)
        } else {
            val result = presensiRef.add(DaftarPresensi(Timestamp.now(), true))
                .await()
                .get()
                .await()
            emit(result.toObject(DaftarPresensi::class.java)!!)
        }
    }

}