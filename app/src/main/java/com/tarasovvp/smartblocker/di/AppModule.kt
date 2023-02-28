package com.tarasovvp.smartblocker.di

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.database.AppDatabase
import com.tarasovvp.smartblocker.database.dao.*
import com.tarasovvp.smartblocker.repository.*
import com.tarasovvp.smartblocker.repositoryImpl.*
import com.tarasovvp.smartblocker.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        AppDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideContactDao(db: AppDatabase) = db.contactDao()

    @Singleton
    @Provides
    fun provideContactRepository(contactDao: ContactDao) : ContactRepository {
        return ContactRepositoryImpl(contactDao)
    }

    @Singleton
    @Provides
    fun provideCountryCodeDao(db: AppDatabase) = db.countryCodeDao()

    @Singleton
    @Provides
    fun provideCountryCodeRepository(countryCodeDao: CountryCodeDao) : CountryCodeRepository {
        return CountryCodeRepositoryImpl(countryCodeDao)
    }

    @Singleton
    @Provides
    fun provideFilterDao(db: AppDatabase) = db.filterDao()

    @Singleton
    @Provides
    fun provideFilterRepository(
        filterDao: FilterDao,
        realDataBaseRepository: RealDataBaseRepository,
    ) : FilterRepository {
        return FilterRepositoryImpl(filterDao, realDataBaseRepository)
    }

    @Singleton
    @Provides
    fun provideLogCallDao(db: AppDatabase) = db.logCallDao()

    @Singleton
    @Provides
    fun provideLogCallRepository(logCallDao: LogCallDao, filterRepository: FilterRepository) : CallRepository {
        return CallRepositoryImpl(logCallDao, filterRepository)
    }

    @Singleton
    @Provides
    fun provideFilteredCallDao(db: AppDatabase) = db.filteredCallDao()

    @Singleton
    @Provides
    fun provideFilteredCallRepository(
        filteredCallDao: FilteredCallDao, realDataBaseRepository: RealDataBaseRepository) : FilteredCallRepository {
        return  FilteredCallRepositoryImpl(filteredCallDao, realDataBaseRepository)
    }

    @Singleton
    @Provides
    fun provideAuthRepository() : AuthRepository {
        return AuthRepositoryImpl(SmartBlockerApp.instance?.auth)
    }

    @Singleton
    @Provides
    fun provideRealDataBaseRepository() : RealDataBaseRepository {
        return RealDataBaseRepositoryImpl(FirebaseDatabase.getInstance(BuildConfig.REALTIME_DATABASE).reference)
    }
}