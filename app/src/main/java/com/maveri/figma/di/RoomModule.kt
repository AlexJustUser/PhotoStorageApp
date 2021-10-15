package com.maveri.figma.di

import android.content.Context
import androidx.room.Room
import com.maveri.figma.model.UserDao
import com.maveri.figma.model.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext


@Module
@InstallIn(ViewModelComponent::class)
object RoomModule {
    @Provides
    fun provideMyDB(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(context,
            UserDatabase::class.java,
            UserDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideMyDao(myDB: UserDatabase): UserDao {
        return myDB.userDao()
    }
}

