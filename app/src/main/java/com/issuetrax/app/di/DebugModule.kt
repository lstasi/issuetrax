package com.issuetrax.app.di

import com.issuetrax.app.data.debug.HttpRequestTrackerImpl
import com.issuetrax.app.domain.debug.HttpRequestTracker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for debug-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DebugModule {
    
    @Binds
    @Singleton
    abstract fun bindHttpRequestTracker(
        impl: HttpRequestTrackerImpl
    ): HttpRequestTracker
}
