package com.tarasovvp.smartblocker.di

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.tarasovvp.smartblocker.BuildConfig
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.data.database.AppDatabase
import com.tarasovvp.smartblocker.data.database.dao.*
import com.tarasovvp.smartblocker.data.repositoryImpl.*
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecase.authorization.login.LoginUseCase
import com.tarasovvp.smartblocker.domain.usecase.authorization.login.LoginUseCaseImpl
import com.tarasovvp.smartblocker.domain.usecase.authorization.sign_up.SignUpUseCase
import com.tarasovvp.smartblocker.domain.usecase.authorization.sign_up.SignUpUseCaseImpl
import com.tarasovvp.smartblocker.domain.usecase.countrycode_search.CountryCodeSearchUseCase
import com.tarasovvp.smartblocker.domain.usecase.countrycode_search.CountryCodeSearchUseCaseImpl
import com.tarasovvp.smartblocker.domain.usecase.main.MainUseCase
import com.tarasovvp.smartblocker.domain.usecase.main.MainUseCaseImpl
import com.tarasovvp.smartblocker.domain.usecase.number.create.CreateFilterUseCase
import com.tarasovvp.smartblocker.domain.usecase.number.create.CreateFilterUseCaseImpl
import com.tarasovvp.smartblocker.domain.usecase.number.details.details_filter.DetailsFilterUseCase
import com.tarasovvp.smartblocker.domain.usecase.number.details.details_filter.DetailsFilterUseCaseImpl
import com.tarasovvp.smartblocker.domain.usecase.number.details.details_number_data.DetailsNumberDataUseCase
import com.tarasovvp.smartblocker.domain.usecase.number.details.details_number_data.DetailsNumberDataUseCaseImpl
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_call.ListCallUseCase
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_call.ListCallUseCaseImpl
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_contact.ListContactUseCase
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_contact.ListContactUseCaseImpl
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_filter.ListFilterUseCase
import com.tarasovvp.smartblocker.domain.usecase.number.list.list_filter.ListFilterUseCaseImpl
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_account.SettingsAccountUseCase
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_account.SettingsAccountUseCaseImpl
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_blocker.SettingsBlockerUseCaseImpl
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_list.SettingsListUseCase
import com.tarasovvp.smartblocker.domain.usecase.settings.settings_list.SettingsListUseCaseImpl
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
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl(SmartBlockerApp.instance?.auth)
    }

    @Singleton
    @Provides
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCaseImpl(authRepository)
    }

    @Singleton
    @Provides
    fun provideSignUpUseCase(authRepository: AuthRepository): SignUpUseCase {
        return SignUpUseCaseImpl(authRepository)
    }

    @Singleton
    @Provides
    fun provideSettingsAccountUseCase(authRepository: AuthRepository): SettingsAccountUseCase {
        return SettingsAccountUseCaseImpl(authRepository)
    }

    @Singleton
    @Provides
    fun provideRealDataBaseRepository(): RealDataBaseRepository {
        return RealDataBaseRepositoryImpl(FirebaseDatabase.getInstance(BuildConfig.REALTIME_DATABASE).reference, SmartBlockerApp.instance?.auth?.currentUser?.uid)
    }

    @Singleton
    @Provides
    fun provideMainUseCase(
        contactRepository: ContactRepository,
        countryCodeRepository: CountryCodeRepository,
        filterRepository: FilterRepository,
        logCallRepository: LogCallRepository,
        filteredCallRepository: FilteredCallRepository,
        realDataBaseRepository: RealDataBaseRepository
    ): MainUseCase {
        return MainUseCaseImpl(
            contactRepository,
            countryCodeRepository,
            filterRepository,
            logCallRepository,
            filteredCallRepository,
            realDataBaseRepository
        )
    }

    @Singleton
    @Provides
    fun provideCountryCodeDao(db: AppDatabase) = db.countryCodeDao()

    @Singleton
    @Provides
    fun provideCountryCodeRepository(countryCodeDao: CountryCodeDao): CountryCodeRepository {
        return CountryCodeRepositoryImpl(countryCodeDao)
    }

    @Singleton
    @Provides
    fun provideCountryCodeSearchUseCase(countryCodeRepository: CountryCodeRepository): CountryCodeSearchUseCase {
        return CountryCodeSearchUseCaseImpl(countryCodeRepository)
    }

    @Singleton
    @Provides
    fun provideFilterDao(db: AppDatabase) = db.filterDao()

    @Singleton
    @Provides
    fun provideFilterRepository(
        filterDao: FilterDao,
        realDataBaseRepository: RealDataBaseRepository,
    ): FilterRepository {
        return FilterRepositoryImpl(filterDao, realDataBaseRepository)
    }

    @Singleton
    @Provides
    fun provideListFilterUseCase(
        filterRepository: FilterRepository
    ): ListFilterUseCase {
        return ListFilterUseCaseImpl(filterRepository)
    }

    @Singleton
    @Provides
    fun provideCreateFilterUseCase(
        contactRepository: ContactRepository,
        countryCodeRepository: CountryCodeRepository,
        filterRepository: FilterRepository,
        logCallRepository: LogCallRepository
    ): CreateFilterUseCase {
        return CreateFilterUseCaseImpl(
            contactRepository,
            countryCodeRepository,
            filterRepository,
            logCallRepository
        )
    }

    @Singleton
    @Provides
    fun provideDetailsFilterUseCase(
        contactRepository: ContactRepository,
        filterRepository: FilterRepository,
        logCallRepository: LogCallRepository,
        filteredCallRepository: FilteredCallRepository
    ): DetailsFilterUseCase {
        return DetailsFilterUseCaseImpl(
            contactRepository,
            filterRepository,
            logCallRepository,
            filteredCallRepository
        )
    }

    @Singleton
    @Provides
    fun provideLogCallDao(db: AppDatabase) = db.logCallDao()

    @Singleton
    @Provides
    fun provideLogCallRepository(logCallDao: LogCallDao): LogCallRepository {
        return LogCallRepositoryImpl(logCallDao)
    }

    @Singleton
    @Provides
    fun provideFilteredCallDao(db: AppDatabase) = db.filteredCallDao()

    @Singleton
    @Provides
    fun provideFilteredCallRepository(
        filteredCallDao: FilteredCallDao, realDataBaseRepository: RealDataBaseRepository
    ): FilteredCallRepository {
        return FilteredCallRepositoryImpl(filteredCallDao, realDataBaseRepository)
    }

    @Singleton
    @Provides
    fun provideContactDao(db: AppDatabase) = db.contactDao()

    @Singleton
    @Provides
    fun provideContactRepository(contactDao: ContactDao): ContactRepository {
        return ContactRepositoryImpl(contactDao)
    }

    @Singleton
    @Provides
    fun provideListContactUseCase(contactRepository: ContactRepository): ListContactUseCase {
        return ListContactUseCaseImpl(contactRepository)
    }

    @Singleton
    @Provides
    fun provideDetailsNumberDataUseCase(
        countryCodeRepository: CountryCodeRepository,
        filterRepository: FilterRepository,
        filteredCallRepository: FilteredCallRepository
    ): DetailsNumberDataUseCase {
        return DetailsNumberDataUseCaseImpl(
            countryCodeRepository,
            filterRepository,
            filteredCallRepository
        )
    }

    @Singleton
    @Provides
    fun provideListCallUseCase(
        logCallRepository: LogCallRepository,
        filteredCallRepository: FilteredCallRepository
    ): ListCallUseCase {
        return ListCallUseCaseImpl(logCallRepository, filteredCallRepository)
    }

    @Singleton
    @Provides
    fun provideSettingsBlockerUseCase(realDataBaseRepository: RealDataBaseRepository): SettingsBlockerUseCase {
        return SettingsBlockerUseCaseImpl(realDataBaseRepository)
    }

    @Singleton
    @Provides
    fun provideSettingsListUseCase(realDataBaseRepository: RealDataBaseRepository): SettingsListUseCase {
        return SettingsListUseCaseImpl(realDataBaseRepository)
    }
}