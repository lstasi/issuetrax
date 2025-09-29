package com.issuetrax.app.di

import com.issuetrax.app.data.repository.AuthRepositoryImpl
import com.issuetrax.app.data.repository.GitHubRepositoryImpl
import com.issuetrax.app.domain.repository.AuthRepository
import com.issuetrax.app.domain.repository.GitHubRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindGitHubRepository(
        gitHubRepositoryImpl: GitHubRepositoryImpl
    ): GitHubRepository
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}