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
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.smartblocker.BuildConfig
import com.tarasovvp.smartblocker.MainNavigationDirections
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.data.repositoryImpl.DataStoreRepositoryImpl
import com.tarasovvp.smartblocker.databinding.ActivityMainBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DIALOG
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.IS_INSTRUMENTAL_TEST
import com.tarasovvp.smartblocker.utils.*
import com.tarasovvp.smartblocker.utils.BackPressedUtil.isBackPressedScreen
import com.tarasovvp.smartblocker.utils.PermissionUtil.checkPermissions
import com.tarasovvp.smartblocker.infrastructure.receivers.CallHandleReceiver
import com.tarasovvp.smartblocker.infrastructure.services.ForegroundCallService
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var binding: ActivityMainBinding? = null
    private var navController: NavController? = null
    private var callHandleReceiver: CallHandleReceiver? = null
    private var callIntent: Intent? = null
    private var isDialog: Boolean = false
    private var adRequest: AdRequest? = null
    private var interstitialAd: InterstitialAd? = null
    private var adIsLoading: Boolean = false
    private var isSavedInstanceStateNull: Boolean? = null
    var bottomNavigationView: BottomNavigationView? = null
    var bottomNavigationDivider: View? = null
    var toolbar: androidx.appcompat.widget.Toolbar? = null

    val mainViewModel: MainViewModel by viewModels()

    var navigationScreens = arrayListOf(
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
        val appLang = runBlocking {
            DataStoreRepositoryImpl(newBase).getAppLang().first()
        } ?: Locale.getDefault().language
        super.attachBaseContext(ContextWrapper(newBase.setAppLocale(appLang)))
    }

    override fun onStart() {
        super.onStart()
        callHandleReceiver = CallHandleReceiver {
            mainViewModel.getAllData()
        }
        registerReceiver(callHandleReceiver, IntentFilter(Constants.CALL_RECEIVE))
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
            setAppLanguage()
            setCurrentTheme()
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

    private fun setAppLanguage() {
        mainViewModel.setAppLanguage()
    }

    private fun setCurrentTheme() {
        mainViewModel.setAppTheme()
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
            this.graph = navGraph
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
        bottomNavigationView?.setOnItemReselectedListener {
        }
        bottomNavigationView?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.listBlockerFragment -> navController?.navigate(R.id.listBlockerFragment)
                R.id.listPermissionFragment -> navController?.navigate(R.id.listPermissionFragment)
                R.id.listContactFragment -> navController?.navigate(R.id.listContactFragment)
                R.id.listCallFragment -> navController?.navigate(R.id.listCallFragment)
            }
            return@setOnItemSelectedListener true
        }
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
            toolbar?.menu?.clear()
            toolbar?.isVisible =
                destination.id notEquals R.id.onBoardingFragment && destination.id notEquals R.id.loginFragment && destination.id notEquals R.id.signUpFragment
            binding?.toolbarDivider?.isVisible = toolbar?.isVisible.isTrue()
            loadAdBanner(toolbar?.isVisible.isTrue() && navigationScreens.contains(destination.id).not())

        }
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
                    if ((application as? SmartBlockerApp)?.isNetworkAvailable.isTrue()) {
                        getAllData()
                    } else {
                        navController?.navigate(R.id.startUnavailableNetworkDialog)
                    }
                }
            }
            blockerTurnOffLiveData.safeSingleObserve(this@MainActivity) { isSmartBlockerTurnOff ->
                if (intent.getBooleanExtra(IS_INSTRUMENTAL_TEST,false).not() && isSmartBlockerTurnOff.not() && isBlockerLaunched().not()) {
                    callIntent = Intent(this@MainActivity, ForegroundCallService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(callIntent)
                    } else {
                        startService(callIntent)
                    }
                }
            }
            currentUserLiveData.safeSingleObserve(this@MainActivity) { currentUser ->
                setCurrentUserData(currentUser)
            }
            successAllDataLiveData.safeSingleObserve(this@MainActivity) {
                setMainProgressVisibility(false)
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
        mainViewModel.getBlockerTurnOff()
    }

    fun stopBlocker() {
        if (callIntent.isNotNull()) stopService(callIntent)
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

    fun getAllData() {
        if (checkPermissions().isTrue()) {
            setMainProgressVisibility(true)
            if (firebaseAuth.currentUser.isNotNull()) {
                mainViewModel.getCurrentUser()
            } else {
                mainViewModel.getAllData()
            }
        } else {
            requestPermissionLauncher.launch(PermissionUtil.permissionsArray())
        }
    }

    fun showInterstitial() {
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
        }
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