package com.issuetrax.app.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.issuetrax.app.data.local.dao.PullRequestDao
import com.issuetrax.app.data.local.entity.PullRequestEntity

@Database(
    entities = [PullRequestEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeConverter::class)
abstract class IssuetraxDatabase : RoomDatabase() {
    
    abstract fun pullRequestDao(): PullRequestDao
    
    companion object {
        const val DATABASE_NAME = "issuetrax_database"
        
        fun create(context: Context): IssuetraxDatabase {
            return Room.databaseBuilder(
                context,
                IssuetraxDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}