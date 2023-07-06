package com.tarasovvp.smartblocker.presentation.main

import android.animation.Animator
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.BuildConfig
import com.tarasovvp.smartblocker.MainNavigationDirections
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.ActivityMainBinding
import com.tarasovvp.smartblocker.di.DataStoreEntryPoint
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DIALOG
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.IS_INSTRUMENTAL_TEST
import com.tarasovvp.smartblocker.infrastructure.receivers.CallHandleReceiver
import com.tarasovvp.smartblocker.infrastructure.services.ForegroundCallService
import com.tarasovvp.smartblocker.presentation.main.number.list.list_call.ListCallFragment
import com.tarasovvp.smartblocker.presentation.main.number.list.list_contact.ListContactFragment
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListBlockerFragment
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListPermissionFragment
import com.tarasovvp.smartblocker.utils.BackPressedUtil.isBackPressedScreen
import com.tarasovvp.smartblocker.utils.PermissionUtil
import com.tarasovvp.smartblocker.utils.PermissionUtil.checkPermissions
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var binding: ActivityMainBinding? = null
    var navController: NavController? = null
    private var callHandleReceiver: CallHandleReceiver? = null
    private var callIntent: Intent? = null
    private var isDialog: Boolean = false
    private var adRequest: AdRequest? = null
    private var interstitialAd: InterstitialAd? = null
    private var adIsLoading: Boolean = false
    private var isSavedInstanceStateNull: Boolean? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var bottomNavigationDivider: View? = null
    var toolbar: androidx.appcompat.widget.Toolbar? = null
    var allDataChangeLiveData: ArrayList<String>? = null

    val mainViewModel: MainViewModel by viewModels()

    private var navigationScreens = arrayListOf(
        R.id.listCallFragment,
        R.id.listContactFragment,
        R.id.listBlockerFragment,
        R.id.listPermissionFragment
    )

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, @JvmSuppressWildcards Boolean>? ->
            if (isGranted?.values?.contains(false).isTrue()) {
                showInfoMessage(getString(R.string.app_need_permissions), true)
            } else {
                getAllData()
            }
        }

    override fun attachBaseContext(newBase: Context) {
        val dataStoreRepository = EntryPointAccessors.fromApplication( newBase, DataStoreEntryPoint::class.java ).dataStoreRepository
        val appTheme = runBlocking {
            dataStoreRepository.getAppTheme().first()
        } ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        AppCompatDelegate.setDefaultNightMode(appTheme)
        val appLang = runBlocking {
            dataStoreRepository.getAppLang().first()
        } ?: Locale.getDefault().language
        super.attachBaseContext(ContextWrapper(newBase.setAppLocale(appLang)))
    }

    override fun onStart() {
        super.onStart()
        callHandleReceiver = CallHandleReceiver {
            mainViewModel.getAllData()
        }
        registerReceiver(callHandleReceiver, IntentFilter(Constants.CALL_RECEIVE))
        if (intent.getBooleanExtra(IS_INSTRUMENTAL_TEST,false).not()) {
            setMobileAds()
        }
    }

    private fun setMobileAds() {
        try {
            MobileAds.initialize(this) {}
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //TODO
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf("33BE2250B43518CCDA7DE426D04EE231")).build()
        MobileAds.setRequestConfiguration(configuration)
        adRequest = AdRequest.Builder().build()
        loadAd()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(callHandleReceiver)
    }

    override fun onPause() {
        binding?.adView?.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding?.adView?.resume()
    }

    override fun onDestroy() {
        binding?.adView?.destroy()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SmartBlocker)
        super.onCreate(savedInstanceState)
        isSavedInstanceStateNull = savedInstanceState.isNull()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setAnimatorListener()
        if (intent.getBooleanExtra(IS_INSTRUMENTAL_TEST,false).not()) {
            mainViewModel.getOnBoardingSeen()
        }
        observeLiveData()
    }

    private fun setAnimatorListener() {
        binding?.mainSplash?.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator) {
                binding?.mainSplash?.isVisible = false
                binding?.mainContainer?.isVisible = true
            }
            override fun onAnimationStart(p0: Animator) {
            }
            override fun onAnimationCancel(p0: Animator) {
            }
            override fun onAnimationRepeat(p0: Animator) {
            }
        })
    }

    private fun setNavigationComponents(isOnBoardingSeen: Boolean) {
        setNavController()
        setStartDestination(isOnBoardingSeen)
        setToolBar()
        setBottomNavigationView()
        setOnDestinationChangedListener()
    }

    private fun setNavController() {
        navController = (supportFragmentManager.findFragmentById(
            R.id.host_main_fragment
        ) as NavHostFragment).navController
    }

    private fun setStartDestination(isOnBoardingSeen: Boolean) {
        navController?.apply {
            val navGraph = this.navInflater.inflate(R.navigation.navigation)
            navGraph.setStartDestination(
                when {
                    isOnBoardingSeen.not() -> R.id.onBoardingFragment
                    firebaseAuth.currentUser.isNotNull() -> R.id.listBlockerFragment
                    else -> R.id.loginFragment
                }
            )
            this.setGraph(navGraph, intent.extras)
        }
    }

    private fun setToolBar() {
        toolbar = binding?.toolbar
        navController?.let { toolbar?.setupWithNavController(it) }
    }

    private fun setBottomNavigationView() {
        bottomNavigationView = binding?.bottomNav
        bottomNavigationDivider = binding?.bottomNavDivider
        navController?.let { bottomNavigationView?.setupWithNavController(it) }
        (bottomNavigationView?.getChildAt(0) as? BottomNavigationMenuView)?.children?.forEach {
            it.findViewById<TextView>(com.google.android.material.R.id.navigation_bar_item_large_label_view)
                .apply {
                    ellipsize = TextUtils.TruncateAt.MARQUEE
                    setSingleLine()
                }
        }
    }

    private fun setOnDestinationChangedListener() {
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            if (navigationScreens.contains(destination.id) || R.id.loginFragment == destination.id) {
                toolbar?.navigationIcon = null
            } else {
                if (destination.navigatorName notEquals DIALOG && isDialog.not()) {
                    toolbar?.navigationIcon =
                        ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
                }
            }
            if (destination.navigatorName == DIALOG || isDialog) {
                isDialog = isDialog.not()
                return@addOnDestinationChangedListener
            }
            setToolbarVisibility(destination)
            checkBottomBarVisibility(destination)
            setToolbarMenu(destination)
            binding?.progressBar?.isVisible = false
            loadAdBanner(toolbar?.isVisible.isTrue() && navigationScreens.contains(destination.id).not())
        }
    }

    private fun setToolbarVisibility(destination: NavDestination) {
        toolbar?.isVisible =
            destination.id notEquals R.id.onBoardingFragment && destination.id notEquals R.id.loginFragment && destination.id notEquals R.id.signUpFragment
        binding?.toolbarDivider?.isVisible = toolbar?.isVisible.isTrue()
    }

    private fun checkBottomBarVisibility(destination: NavDestination) {
        bottomNavigationView?.isVisible = try {
            navigationScreens.contains(destination.id)
        } catch (e: Exception) {
            false
        }
        bottomNavigationDivider?.isVisible = bottomNavigationView?.isVisible.isTrue()
    }

    private fun setToolbarMenu(destination: NavDestination) {
        toolbar?.menu?.clear()
        if (navigationScreens.contains(destination.id)) toolbar?.inflateMenu(R.menu.toolbar_search)
    }

    private fun loadAdBanner(isLoading: Boolean) {
        binding?.adView?.apply {
            if (isLoading) {
                adRequest?.let {
                    loadAd(it)
                }
            } else {
                destroy()
            }
            isVisible = isLoading
        }
    }

    private fun observeLiveData() {
        with(mainViewModel) {
            onBoardingSeenLiveData.safeSingleObserve(this@MainActivity) { isOnBoardingSeen ->
                setNavigationComponents(isOnBoardingSeen)
                if (isOnBoardingSeen && firebaseAuth.currentUser.isNotNull() && isSavedInstanceStateNull.isTrue()) {
                    startBlocker()
                    if (application.isNetworkAvailable()) {
                        setMainProgressVisibility(true)
                        getCurrentUser()
                    } else {
                        navController?.navigate(R.id.startUnavailableNetworkDialog)
                    }
                }
            }
            blockerTurnOnLiveData.safeSingleObserve(this@MainActivity) { blockerTurnOn ->
                if (intent.getBooleanExtra(IS_INSTRUMENTAL_TEST,false).not() && blockerTurnOn && isBlockerLaunched().not()) {
                    callIntent = Intent(this@MainActivity, ForegroundCallService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(callIntent)
                    } else {
                        startService(callIntent)
                    }
                }
            }
            successAllDataLiveData.safeSingleObserve(this@MainActivity) {
                setMainProgressVisibility(false)
                allDataChangeLiveData = arrayListOf(ListBlockerFragment::class.java.simpleName, ListPermissionFragment::class.java.simpleName, ListCallFragment::class.java.simpleName, ListContactFragment::class.java.simpleName)
            }
            exceptionLiveData.safeSingleObserve(this@MainActivity) { errorMessage ->
                showInfoMessage(errorMessage, true)
                setMainProgressVisibility(false)
            }
            progressStatusLiveData.safeSingleObserve(this@MainActivity) { mainProgress ->
                binding?.mainProgressBarAnimation?.mainProgress = mainProgress
            }
        }
    }

    fun startBlocker() {
        mainViewModel.getBlockerTurnOn()
    }

    fun stopBlocker() {
        if (callIntent.isNotNull()) {
            stopService(callIntent)
            callIntent = null
        }
    }

    private fun isBlockerLaunched(): Boolean {
        return callIntent.isNotNull()
    }

    fun showInfoMessage(message: String, isError: Boolean) {
        this.showMessage(message, isError)
    }

    fun setMainProgressVisibility(isVisible: Boolean) {
        binding?.mainProgressBarAnimation?.mainProgressBarContainer?.isVisible = isVisible
    }

    fun setProgressVisibility(isVisible: Boolean) {
        binding?.progressBar?.isVisible = isVisible
    }

    fun getAllData(isInit: Boolean = false) {
        if (intent.getBooleanExtra(IS_INSTRUMENTAL_TEST,false)) return
        if (checkPermissions().isTrue()) {
            setMainProgressVisibility(true)
            if (firebaseAuth.isAuthorisedUser()) {
                mainViewModel.getCurrentUser(isInit)
            } else {
                mainViewModel.getAllData(isInit)
            }
        } else {
            requestPermissionLauncher.launch(PermissionUtil.permissionsArray())
        }
    }

    fun showInterstitial() {
        //TODO
        /*if (intent.getBooleanExtra(IS_INSTRUMENTAL_TEST,false)) return
        interstitialAd?.apply {
            fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        interstitialAd = null
                        loadAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        interstitialAd = null
                    }

                    override fun onAdShowedFullScreenContent() {
                    }
                }
            show(this@MainActivity)
        }*/
    }

    private fun loadAd() {

        adRequest?.let {
            //TODO interstitial
            InterstitialAd.load(
                this,
                BuildConfig.INTERSTITIAL_AD,
                it,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        interstitialAd = null
                        adIsLoading = false
                        val error =
                            "domain: ${adError.domain}, code: ${adError.code}, " + "message: ${adError.message}"
                        showInfoMessage("onAdFailedToLoad() with error $error", true)
                    }

                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        adIsLoading = false
                    }
                }
            )
        }
    }

    //TODO
    override fun onBackPressed() {
        if (navController?.isBackPressedScreen().isTrue()) {
            navController?.navigate(MainNavigationDirections.startAppExitDialog())
        } else {
            navController?.popBackStack()
        }
    }
}