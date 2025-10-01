package com.issuetrax.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.issuetrax.app.data.api.GitHubApiService
import com.issuetrax.app.data.local.database.IssuetraxDatabase
import com.issuetrax.app.data.local.dao.PullRequestDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): IssuetraxDatabase {
        return IssuetraxDatabase.create(context)
    }
    
    @Provides
    fun providePullRequestDao(database: IssuetraxDatabase): PullRequestDao {
        return database.pullRequestDao()
    }
    
    @Provides
    @Singleton
    fun provideGitHubApiService(retrofit: Retrofit): GitHubApiService {
        return retrofit.create(GitHubApiService::class.java)
    }
}