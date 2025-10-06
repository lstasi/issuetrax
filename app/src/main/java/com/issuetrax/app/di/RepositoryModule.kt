package com.issuetrax.app.di

import com.issuetrax.app.data.repository.AuthRepositoryImpl
import com.issuetrax.app.data.repository.GitHubRepositoryImpl
import com.issuetrax.app.data.repository.RepositoryContextRepositoryImpl
import com.issuetrax.app.domain.repository.AuthRepository
import com.issuetrax.app.domain.repository.GitHubRepository
import com.issuetrax.app.domain.repository.RepositoryContextRepository
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
    
    @Binds
    @Singleton
    abstract fun bindRepositoryContextRepository(
        repositoryContextRepositoryImpl: RepositoryContextRepositoryImpl
    ): RepositoryContextRepository
}