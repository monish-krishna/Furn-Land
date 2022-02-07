package com.practise.furn_land.di

import android.app.Application
import androidx.room.Room
import com.practise.furn_land.data.database.FurnitureDatabase
import com.practise.furn_land.data.repository.FurnitureRepository
import com.practise.furn_land.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFurnitureDatabase(app : Application) : FurnitureDatabase{
        return Room.databaseBuilder(
            app,
            FurnitureDatabase::class.java,
            "furnitures_shopping"
        ).createFromAsset("database/furnitures_shopping.db").build()
    }

    @Provides
    @Singleton
    fun provideFurnitureRepository(db : FurnitureDatabase) : FurnitureRepository{
        return FurnitureRepository(db.furnitureDao)
    }

    @Provides
    @Singleton
    fun provideUserRepository(db : FurnitureDatabase) : UserRepository{
        return UserRepository(db.userDao)
    }
}