package com.papb.buanaabsensi.di

import com.papb.buanaabsensi.data.repository.AuthRepoImpl
import com.papb.buanaabsensi.data.repository.PresensiRepoImpl
import com.papb.buanaabsensi.domain.AuthRepo
import com.papb.buanaabsensi.domain.PresensiRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Singleton
    @Binds
    abstract fun bindAuthRepo(authRepoImpl: AuthRepoImpl): AuthRepo

    @Singleton
    @Binds
    abstract fun bindPresensiRepo(presensiRepoImpl: PresensiRepoImpl): PresensiRepo

}