package com.papb.buanaabsensi.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.papb.buanaabsensi.data.model.Pegawai
import com.papb.buanaabsensi.domain.AuthRepo
import com.papb.buanaabsensi.util.Resource
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore
) : AuthRepo {

    private val userRef = firebaseFirestore.collection("user")

    override suspend fun getUserData() = flow {
        withTimeout(10000L) {
            val taskResult = userRef
                .whereEqualTo("uid", firebaseAuth.currentUser!!.uid)
                .get()
                .await()
            if (taskResult.documents.isNotEmpty()) {
                emit(Resource.Success(taskResult.documents[0].toObject(Pegawai::class.java)!!))
            } else {
                emit(Resource.Failure(Exception("Data Tidak Ditemukan"), null))
            }
        }
    }

    override suspend fun checkLogin() = flow {
        withTimeout(5000L) {
            emit(Resource.Success(firebaseAuth.currentUser != null))
        }
    }

    override suspend fun checkNipAvail(nip: String) = flow<Resource<Boolean>> {
        val taskResult = userRef.document(nip).get().await()
        emit(Resource.Success(taskResult.exists()))
    }

    override suspend fun daftarAkun(pegawai: Pegawai) = flow {
        try {
            val map = HashMap<String, Any?>()
            map["email"] = pegawai.email
            map["isActive"] = pegawai.isActive
            map["namaDepan"] = pegawai.namaDepan
            map["namaBelakang"] = pegawai.namaBelakang
            map["uid"] = pegawai.uid
            userRef.document(pegawai.nip!!).update(map).await()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            Timber.e(e)
            firebaseAuth.currentUser?.let { user ->
                Timber.i("Store New User Data Failed, Deleting User..")
                user.delete().await()
            }
            emit(Resource.Failure(e, null))
        }
    }

    override suspend fun tambahNip(nip: String) = flow {
        userRef.document(nip).set(Pegawai(nip = nip, createdAt = Timestamp.now())).await()
        emit(nip)
    }

}