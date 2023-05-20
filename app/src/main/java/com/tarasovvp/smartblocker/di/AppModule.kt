package com.tarasovvp.smartblocker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tarasovvp.smartblocker.BuildConfig
import com.tarasovvp.smartblocker.data.database.AppDatabase
import com.tarasovvp.smartblocker.data.database.dao.*
import com.tarasovvp.smartblocker.data.repositoryImpl.*
import com.tarasovvp.smartblocker.domain.repository.*
import com.tarasovvp.smartblocker.domain.usecases.*
import com.tarasovvp.smartblocker.presentation.main.authorization.login.LoginUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.authorization.sign_up.SignUpUseCaseImpl
import com.tarasovvp.smartblocker.presentation.dialogs.country_code_search_dialog.CountryCodeSearchUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.MainUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.authorization.onboarding.OnBoardingUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.number.create.CreateFilterUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.number.details.details_filter.DetailsFilterUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data.DetailsNumberDataUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.number.list.list_call.ListCallUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.number.list.list_contact.ListContactUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListFilterUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.settings.settings_account.SettingsAccountUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker.SettingsBlockerUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.settings.settings_language.SettingsLanguageUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.settings.settings_list.SettingsListUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.settings.settings_theme.SettingsThemeUseCaseImpl
import com.tarasovvp.smartblocker.presentation.mapperImpl.*
import com.tarasovvp.smartblocker.presentation.mappers.*
import com.tarasovvp.smartblocker.utils.AppPhoneNumberUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(produceFile = { context.preferencesDataStoreFile(context.packageName) })
    }

    @Singleton
    @Provides
    fun provideDataStoreRepository(dataStore: DataStore<Preferences>):  DataStoreRepository {
        return DataStoreRepositoryImpl(dataStore)
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.SERVER_CLIENT_ID)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance(BuildConfig.REALTIME_DATABASE)
    }

    @Singleton
    @Provides
    fun providePhoneNumberUtil(): AppPhoneNumberUtil {
        return AppPhoneNumberUtil()
    }

    @Singleton
    @Provides
    fun provideOnBoardingUseCase(dataStoreRepository: DataStoreRepository): OnBoardingUseCase {
        return OnBoardingUseCaseImpl(dataStoreRepository)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(firebaseAuth: FirebaseAuth, googleSignInClient: GoogleSignInClient): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, googleSignInClient)
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
    fun provideRealDataBaseRepository(firebaseDatabase: FirebaseDatabase, firebaseAuth: FirebaseAuth): RealDataBaseRepository {
        return RealDataBaseRepositoryImpl(firebaseDatabase, firebaseAuth)
    }

    @Singleton
    @Provides
    fun provideMainUseCase(
        contactRepository: ContactRepository,
        countryCodeRepository: CountryCodeRepository,
        filterRepository: FilterRepository,
        logCallRepository: LogCallRepository,
        filteredCallRepository: FilteredCallRepository,
        realDataBaseRepository: RealDataBaseRepository,
        dataStoreRepository: DataStoreRepository
    ): MainUseCase {
        return MainUseCaseImpl(
            contactRepository,
            countryCodeRepository,
            filterRepository,
            logCallRepository,
            filteredCallRepository,
            realDataBaseRepository,
            dataStoreRepository
        )
    }

    @Singleton
    @Provides
    fun provideCountryCodeDao(db: AppDatabase) = db.countryCodeDao()

    @Singleton
    @Provides
    fun provideCountryCodeRepository(appPhoneNumberUtil: AppPhoneNumberUtil, countryCodeDao: CountryCodeDao): CountryCodeRepository {
        return CountryCodeRepositoryImpl(appPhoneNumberUtil, countryCodeDao)
    }

    @Singleton
    @Provides
    fun provideCountryCodeUIMapper(dataStoreRepository: DataStoreRepository): CountryCodeUIMapper {
        return CountryCodeUIMapperImpl(dataStoreRepository)
    }

    @Singleton
    @Provides
    fun provideCountryCodeSearchUseCase(countryCodeRepository: CountryCodeRepository, dataStoreRepository: DataStoreRepository): CountryCodeSearchUseCase {
        return CountryCodeSearchUseCaseImpl(countryCodeRepository, dataStoreRepository)
    }

    @Singleton
    @Provides
    fun provideFilterDao(db: AppDatabase) = db.filterDao()

    @Singleton
    @Provides
    fun provideFilterRepository(
        filterDao: FilterDao
    ): FilterRepository {
        return FilterRepositoryImpl(filterDao)
    }

    @Singleton
    @Provides
    fun provideFilterWithFilteredNumberUIMapper(): FilterWithFilteredNumberUIMapper {
        return FilterWithFilteredNumberUIMapperImpl()
    }

    @Singleton
    @Provides
    fun provideListFilterUseCase(
        filterRepository: FilterRepository,
        realDataBaseRepository: RealDataBaseRepository,
        firebaseAuth: FirebaseAuth
    ): ListFilterUseCase {
        return ListFilterUseCaseImpl(filterRepository, realDataBaseRepository, firebaseAuth)
    }

    @Singleton
    @Provides
    fun provideCreateFilterUseCase(
        contactRepository: ContactRepository,
        countryCodeRepository: CountryCodeRepository,
        filterRepository: FilterRepository,
        realDataBaseRepository: RealDataBaseRepository,
        logCallRepository: LogCallRepository,
        firebaseAuth: FirebaseAuth
    ): CreateFilterUseCase {
        return CreateFilterUseCaseImpl(
            contactRepository,
            countryCodeRepository,
            filterRepository,
            realDataBaseRepository,
            logCallRepository,
            firebaseAuth
        )
    }

    @Singleton
    @Provides
    fun provideDetailsFilterUseCase(
        contactRepository: ContactRepository,
        filterRepository: FilterRepository,
        realDataBaseRepository: RealDataBaseRepository,
        logCallRepository: LogCallRepository,
        filteredCallRepository: FilteredCallRepository,
        firebaseAuth: FirebaseAuth
    ): DetailsFilterUseCase {
        return DetailsFilterUseCaseImpl(
            contactRepository,
            filterRepository,
            realDataBaseRepository,
            logCallRepository,
            filteredCallRepository,
            firebaseAuth
        )
    }

    @Singleton
    @Provides
    fun provideLogCallDao(db: AppDatabase) = db.logCallDao()

    @Singleton
    @Provides
    fun provideLogCallRepository(appPhoneNumberUtil: AppPhoneNumberUtil, logCallDao: LogCallDao): LogCallRepository {
        return LogCallRepositoryImpl(appPhoneNumberUtil, logCallDao)
    }

    @Singleton
    @Provides
    fun provideFilteredCallDao(db: AppDatabase) = db.filteredCallDao()

    @Singleton
    @Provides
    fun provideFilteredCallRepository(
        filteredCallDao: FilteredCallDao
    ): FilteredCallRepository {
        return FilteredCallRepositoryImpl(filteredCallDao)
    }

    @Singleton
    @Provides
    fun provideCallWithFilterUIMapper(filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper): CallWithFilterUIMapper {
        return CallWithFilterUIMapperImpl(filterWithFilteredNumberUIMapper)
    }

    @Singleton
    @Provides
    fun provideListCallUseCase(
        logCallRepository: LogCallRepository,
        filteredCallRepository: FilteredCallRepository,
        realDataBaseRepository: RealDataBaseRepository,
        firebaseAuth: FirebaseAuth
    ): ListCallUseCase {
        return ListCallUseCaseImpl(logCallRepository, filteredCallRepository, realDataBaseRepository, firebaseAuth)
    }


    @Singleton
    @Provides
    fun provideContactDao(db: AppDatabase) = db.contactDao()

    @Singleton
    @Provides
    fun provideContactRepository(appPhoneNumberUtil: AppPhoneNumberUtil, contactDao: ContactDao): ContactRepository {
        return ContactRepositoryImpl(appPhoneNumberUtil, contactDao)
    }

    @Singleton
    @Provides
    fun provideContactWithFilterUIMapper(filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper): ContactWithFilterUIMapper {
        return ContactWithFilterUIMapperImpl(filterWithFilteredNumberUIMapper)
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
        filteredCallRepository: FilteredCallRepository,
        dataStoreRepository: DataStoreRepository
    ): DetailsNumberDataUseCase {
        return DetailsNumberDataUseCaseImpl(
            countryCodeRepository,
            filterRepository,
            filteredCallRepository,
            dataStoreRepository
        )
    }

    @Singleton
    @Provides
    fun provideSettingsBlockerUseCase(realDataBaseRepository: RealDataBaseRepository, dataStoreRepository: DataStoreRepository, firebaseAuth: FirebaseAuth): SettingsBlockerUseCase {
        return SettingsBlockerUseCaseImpl(realDataBaseRepository, dataStoreRepository, firebaseAuth)
    }

    @Singleton
    @Provides
    fun provideSettingsListUseCase(dataStoreRepository: DataStoreRepository, realDataBaseRepository: RealDataBaseRepository): SettingsListUseCase {
        return SettingsListUseCaseImpl(dataStoreRepository, realDataBaseRepository)
    }

    @Singleton
    @Provides
    fun provideSettingsLanguageUseCase(dataStoreRepository: DataStoreRepository): SettingsLanguageUseCase {
        return SettingsLanguageUseCaseImpl(dataStoreRepository)
    }

    @Singleton
    @Provides
    fun provideSettingsThemeUseCase(dataStoreRepository: DataStoreRepository): SettingsThemeUseCase {
        return SettingsThemeUseCaseImpl(dataStoreRepository)
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DataStoreEntryPoint {
    val dataStoreRepository: DataStoreRepository
}