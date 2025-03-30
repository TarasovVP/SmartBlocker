package com.tarasovvp.smartblocker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tarasovvp.smartblocker.BuildConfig
import com.tarasovvp.smartblocker.data.database.AppDatabase
import com.tarasovvp.smartblocker.data.database.dao.ContactDao
import com.tarasovvp.smartblocker.data.database.dao.CountryCodeDao
import com.tarasovvp.smartblocker.data.database.dao.FilterDao
import com.tarasovvp.smartblocker.data.database.dao.FilteredCallDao
import com.tarasovvp.smartblocker.data.database.dao.LogCallDao
import com.tarasovvp.smartblocker.data.repositoryImpl.AuthRepositoryImpl
import com.tarasovvp.smartblocker.data.repositoryImpl.ContactRepositoryImpl
import com.tarasovvp.smartblocker.data.repositoryImpl.CountryCodeRepositoryImpl
import com.tarasovvp.smartblocker.data.repositoryImpl.DataStoreRepositoryImpl
import com.tarasovvp.smartblocker.data.repositoryImpl.FilterRepositoryImpl
import com.tarasovvp.smartblocker.data.repositoryImpl.FilteredCallRepositoryImpl
import com.tarasovvp.smartblocker.data.repositoryImpl.LogCallRepositoryImpl
import com.tarasovvp.smartblocker.data.repositoryImpl.RealDataBaseRepositoryImpl
import com.tarasovvp.smartblocker.domain.repository.AuthRepository
import com.tarasovvp.smartblocker.domain.repository.ContactRepository
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.domain.repository.DataStoreRepository
import com.tarasovvp.smartblocker.domain.repository.FilterRepository
import com.tarasovvp.smartblocker.domain.repository.FilteredCallRepository
import com.tarasovvp.smartblocker.domain.repository.LogCallRepository
import com.tarasovvp.smartblocker.domain.repository.RealDataBaseRepository
import com.tarasovvp.smartblocker.domain.usecases.CountryCodeSearchUseCase
import com.tarasovvp.smartblocker.domain.usecases.CreateFilterUseCase
import com.tarasovvp.smartblocker.domain.usecases.DetailsFilterUseCase
import com.tarasovvp.smartblocker.domain.usecases.DetailsNumberDataUseCase
import com.tarasovvp.smartblocker.domain.usecases.ListCallUseCase
import com.tarasovvp.smartblocker.domain.usecases.ListContactUseCase
import com.tarasovvp.smartblocker.domain.usecases.ListFilterUseCase
import com.tarasovvp.smartblocker.domain.usecases.LoginUseCase
import com.tarasovvp.smartblocker.domain.usecases.MainUseCase
import com.tarasovvp.smartblocker.domain.usecases.OnBoardingUseCase
import com.tarasovvp.smartblocker.domain.usecases.SettingsAccountUseCase
import com.tarasovvp.smartblocker.domain.usecases.SettingsBlockerUseCase
import com.tarasovvp.smartblocker.domain.usecases.SettingsLanguageUseCase
import com.tarasovvp.smartblocker.domain.usecases.SettingsListUseCase
import com.tarasovvp.smartblocker.domain.usecases.SettingsPrivacyPolicyUseCase
import com.tarasovvp.smartblocker.domain.usecases.SettingsSignUpUseCase
import com.tarasovvp.smartblocker.domain.usecases.SettingsThemeUseCase
import com.tarasovvp.smartblocker.domain.usecases.SignUpUseCase
import com.tarasovvp.smartblocker.presentation.dialogs.countrycodesearchdialog.CountryCodeSearchUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.MainUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.authorization.login.LoginUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.authorization.onboarding.OnBoardingUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.authorization.signup.SignUpUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.number.create.CreateFilterUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.number.details.detailsfilter.DetailsFilterUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.number.details.detailsnumberdata.DetailsNumberDataUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.number.list.listcall.ListCallUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.number.list.listcontact.ListContactUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.number.list.listfilter.ListFilterUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.settings.settingsaccount.SettingsAccountUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.settings.settingsblocker.SettingsBlockerUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.settings.settingslanguage.SettingsLanguageUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.settings.settingslist.SettingsListUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.settings.settingsprivacy.SettingsPrivacyPolicyUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.settings.settingssignup.SettingsSignUpUseCaseImpl
import com.tarasovvp.smartblocker.presentation.main.settings.settingstheme.SettingsThemeUseCaseImpl
import com.tarasovvp.smartblocker.presentation.mapperImpl.CallWithFilterUIMapperImpl
import com.tarasovvp.smartblocker.presentation.mapperImpl.ContactWithFilterUIMapperImpl
import com.tarasovvp.smartblocker.presentation.mapperImpl.CountryCodeUIMapperImpl
import com.tarasovvp.smartblocker.presentation.mapperImpl.FilterWithFilteredNumberUIMapperImpl
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.ContactWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.utils.AppPhoneNumberUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Avoid duplicate data store exception in instrumented tests
    private var dataStore: DataStore<Preferences>? = null

    @Singleton
    @Provides
    fun providePreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> {
        if (dataStore == null) {
            dataStore =
                PreferenceDataStoreFactory.create(produceFile = {
                    context.preferencesDataStoreFile(context.packageName)
                })
        }
        return dataStore!!
    }

    @Singleton
    @Provides
    fun provideDataStoreRepository(dataStore: DataStore<Preferences>): DataStoreRepository {
        return DataStoreRepositoryImpl(dataStore)
    }

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext appContext: Context,
    ): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context,
    ): GoogleSignInClient {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        googleSignInClient: GoogleSignInClient,
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, googleSignInClient)
    }

    @Singleton
    @Provides
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCaseImpl(authRepository)
    }

    @Singleton
    @Provides
    fun provideSignUpUseCase(
        authRepository: AuthRepository,
        realDataBaseRepository: RealDataBaseRepository,
    ): SignUpUseCase {
        return SignUpUseCaseImpl(authRepository, realDataBaseRepository)
    }

    @Singleton
    @Provides
    fun provideSettingsAccountUseCase(
        authRepository: AuthRepository,
        realDataBaseRepository: RealDataBaseRepository,
        dataStoreRepository: DataStoreRepository,
    ): SettingsAccountUseCase {
        return SettingsAccountUseCaseImpl(
            authRepository,
            realDataBaseRepository,
            dataStoreRepository,
        )
    }

    @Singleton
    @Provides
    fun provideRealDataBaseRepository(
        firebaseDatabase: FirebaseDatabase,
        firebaseAuth: FirebaseAuth,
    ): RealDataBaseRepository {
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
        dataStoreRepository: DataStoreRepository,
    ): MainUseCase {
        return MainUseCaseImpl(
            contactRepository,
            countryCodeRepository,
            filterRepository,
            logCallRepository,
            filteredCallRepository,
            realDataBaseRepository,
            dataStoreRepository,
        )
    }

    @Singleton
    @Provides
    fun provideCountryCodeDao(db: AppDatabase) = db.countryCodeDao()

    @Singleton
    @Provides
    fun provideCountryCodeRepository(
        appPhoneNumberUtil: AppPhoneNumberUtil,
        countryCodeDao: CountryCodeDao,
    ): CountryCodeRepository {
        return CountryCodeRepositoryImpl(appPhoneNumberUtil, countryCodeDao)
    }

    @Singleton
    @Provides
    fun provideCountryCodeUIMapper(dataStoreRepository: DataStoreRepository): CountryCodeUIMapper {
        return CountryCodeUIMapperImpl(dataStoreRepository)
    }

    @Singleton
    @Provides
    fun provideCountryCodeSearchUseCase(
        countryCodeRepository: CountryCodeRepository,
        dataStoreRepository: DataStoreRepository,
    ): CountryCodeSearchUseCase {
        return CountryCodeSearchUseCaseImpl(countryCodeRepository, dataStoreRepository)
    }

    @Singleton
    @Provides
    fun provideFilterDao(db: AppDatabase) = db.filterDao()

    @Singleton
    @Provides
    fun provideFilterRepository(filterDao: FilterDao): FilterRepository {
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
        firebaseAuth: FirebaseAuth,
        dataStoreRepository: DataStoreRepository,
    ): ListFilterUseCase {
        return ListFilterUseCaseImpl(
            filterRepository,
            realDataBaseRepository,
            firebaseAuth,
            dataStoreRepository,
        )
    }

    @Singleton
    @Provides
    fun provideCreateFilterUseCase(
        contactRepository: ContactRepository,
        phoneNumberUtil: AppPhoneNumberUtil,
        filterRepository: FilterRepository,
        realDataBaseRepository: RealDataBaseRepository,
        firebaseAuth: FirebaseAuth,
    ): CreateFilterUseCase {
        return CreateFilterUseCaseImpl(
            contactRepository,
            phoneNumberUtil,
            filterRepository,
            realDataBaseRepository,
            firebaseAuth,
        )
    }

    @Singleton
    @Provides
    fun provideDetailsFilterUseCase(
        contactRepository: ContactRepository,
        filterRepository: FilterRepository,
        realDataBaseRepository: RealDataBaseRepository,
        filteredCallRepository: FilteredCallRepository,
        firebaseAuth: FirebaseAuth,
    ): DetailsFilterUseCase {
        return DetailsFilterUseCaseImpl(
            contactRepository,
            filterRepository,
            realDataBaseRepository,
            filteredCallRepository,
            firebaseAuth,
        )
    }

    @Singleton
    @Provides
    fun provideLogCallDao(db: AppDatabase) = db.logCallDao()

    @Singleton
    @Provides
    fun provideLogCallRepository(
        appPhoneNumberUtil: AppPhoneNumberUtil,
        logCallDao: LogCallDao,
    ): LogCallRepository {
        return LogCallRepositoryImpl(appPhoneNumberUtil, logCallDao)
    }

    @Singleton
    @Provides
    fun provideFilteredCallDao(db: AppDatabase) = db.filteredCallDao()

    @Singleton
    @Provides
    fun provideFilteredCallRepository(filteredCallDao: FilteredCallDao): FilteredCallRepository {
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
        firebaseAuth: FirebaseAuth,
        dataStoreRepository: DataStoreRepository,
    ): ListCallUseCase {
        return ListCallUseCaseImpl(
            logCallRepository,
            filteredCallRepository,
            realDataBaseRepository,
            firebaseAuth,
            dataStoreRepository,
        )
    }

    @Singleton
    @Provides
    fun provideContactDao(db: AppDatabase) = db.contactDao()

    @Singleton
    @Provides
    fun provideContactRepository(
        appPhoneNumberUtil: AppPhoneNumberUtil,
        contactDao: ContactDao,
    ): ContactRepository {
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
        dataStoreRepository: DataStoreRepository,
    ): DetailsNumberDataUseCase {
        return DetailsNumberDataUseCaseImpl(
            countryCodeRepository,
            filterRepository,
            filteredCallRepository,
            dataStoreRepository,
        )
    }

    @Singleton
    @Provides
    fun provideSettingsBlockerUseCase(
        realDataBaseRepository: RealDataBaseRepository,
        dataStoreRepository: DataStoreRepository,
        firebaseAuth: FirebaseAuth,
    ): SettingsBlockerUseCase {
        return SettingsBlockerUseCaseImpl(realDataBaseRepository, dataStoreRepository, firebaseAuth)
    }

    @Singleton
    @Provides
    fun provideSettingsSignUpUseCase(
        filterRepository: FilterRepository,
        filteredCallRepository: FilteredCallRepository,
        dataStoreRepository: DataStoreRepository,
        authRepository: AuthRepository,
        realDataBaseRepository: RealDataBaseRepository,
    ): SettingsSignUpUseCase {
        return SettingsSignUpUseCaseImpl(
            filterRepository,
            filteredCallRepository,
            dataStoreRepository,
            authRepository,
            realDataBaseRepository,
        )
    }

    @Singleton
    @Provides
    fun provideSettingsListUseCase(
        dataStoreRepository: DataStoreRepository,
        realDataBaseRepository: RealDataBaseRepository,
    ): SettingsListUseCase {
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

    @Singleton
    @Provides
    fun provideSettingsPrivacyPolicyUseCase(
        dataStoreRepository: DataStoreRepository,
        realDataBaseRepository: RealDataBaseRepository,
    ): SettingsPrivacyPolicyUseCase {
        return SettingsPrivacyPolicyUseCaseImpl(dataStoreRepository, realDataBaseRepository)
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DataStoreEntryPoint {
    val dataStoreRepository: DataStoreRepository
}
