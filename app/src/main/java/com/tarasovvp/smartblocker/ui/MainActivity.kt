package com.tarasovvp.smartblocker.ui

import android.animation.Animator
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
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
import com.tarasovvp.smartblocker.MainNavigationDirections
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.constants.Constants.DIALOG
import com.tarasovvp.smartblocker.databinding.ActivityMainBinding
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import com.tarasovvp.smartblocker.utils.*
import com.tarasovvp.smartblocker.utils.BackPressedUtil.isBackPressedScreen
import com.tarasovvp.smartblocker.utils.PermissionUtil.checkPermissions
import java.util.*

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var navController: NavController? = null
    var bottomNavigationView: BottomNavigationView? = null
    var toolbar: androidx.appcompat.widget.Toolbar? = null

    val mainViewModel: MainViewModel by viewModels()

    private var exceptionReceiver: ExceptionReceiver? = null
    private var callHandleReceiver: CallHandleReceiver? = null
    private var callIntent: Intent? = null
    private var callReceiver: CallReceiver? = null
    private var isDialog: Boolean = false
    private var adRequest: AdRequest? = null
    private var interstitialAd: InterstitialAd? = null
    private var adIsLoading: Boolean = false

    var navigationScreens = arrayListOf(
        R.id.listCallFragment,
        R.id.listContactFragment,
        R.id.listBlockerFragment,
        R.id.listPermissionFragment
    )
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, @JvmSuppressWildcards Boolean>? ->
            if (isGranted?.values?.contains(false).isTrue()) {
                Toast.makeText(this, getString(R.string.app_need_permissions), Toast.LENGTH_SHORT)
                    .show()
            } else {
                getAllData()
            }
        }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper(newBase.setAppLocale(SharedPreferencesUtil.appLang
            ?: Locale.getDefault().language)))
    }

    override fun onStart() {
        super.onStart()
        SharedPreferencesUtil.countryCode = SharedPreferencesUtil.countryCode ?: getUserCountry()
        callHandleReceiver = CallHandleReceiver {
            mainViewModel.getAllData()
        }
        registerReceiver(callHandleReceiver, IntentFilter(Constants.CALL_RECEIVE))
        exceptionReceiver = ExceptionReceiver { exception ->
            mainViewModel.exceptionLiveData.postValue(exception)
            setProgressVisibility(false)
        }
        registerReceiver(exceptionReceiver, IntentFilter(Constants.EXCEPTION))
        MobileAds.initialize(this) {}
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf("33BE2250B43518CCDA7DE426D04EE231")).build()
        MobileAds.setRequestConfiguration(configuration)
        adRequest = AdRequest.Builder().build()
        loadAd()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(callHandleReceiver)
        unregisterReceiver(exceptionReceiver)
        callReceiver?.apply {
            unregisterReceiver(this)
        }
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
        setTheme(R.style.Theme_Blacklister)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding?.mainSplash?.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                binding?.mainSplash?.isVisible = false
                binding?.mainContainer?.isVisible = true
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })
        setNavController()
        setToolBar()
        setBottomNavigationView()
        setOnDestinationChangedListener()
        observeLiveData()
        if (SharedPreferencesUtil.isOnBoardingSeen
            && SmartBlockerApp.instance?.isLoggedInUser().isTrue()
            && savedInstanceState.isNull()
        ) {
            if (SmartBlockerApp.instance?.isNetworkAvailable.isNotTrue()) {
                navController?.navigate(R.id.startUnavailableNetworkDialog)
            } else {
                if (SharedPreferencesUtil.blockTurnOff.not() && isBlockerLaunched().not()) startBlocker()
                getAllData()
            }
        }
    }

    private fun setNavController() {
        navController = (supportFragmentManager.findFragmentById(
            R.id.host_main_fragment
        ) as NavHostFragment).navController
        navController?.apply {
            val navGraph = this.navInflater.inflate(R.navigation.navigation)
            navGraph.setStartDestination(
                when {
                    SharedPreferencesUtil.isOnBoardingSeen.not() -> R.id.onBoardingFragment
                    SmartBlockerApp.instance?.isLoggedInUser().isTrue() -> {
                        R.id.listBlockerFragment
                    }
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
        (bottomNavigationView?.getChildAt(0) as BottomNavigationMenuView).children.forEach {
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
                if (destination.navigatorName != DIALOG && isDialog.not()) {
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
                destination.id != R.id.onBoardingFragment && destination.id != R.id.loginFragment && destination.id != R.id.signUpFragment
            loadAdBanner(toolbar?.isVisible.isTrue() && destination.id != R.id.listBlockerFragment && destination.id != R.id.listPermissionFragment)

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

    fun startBlocker() {
        callReceiver?.apply {
            unregisterReceiver(this)
            callReceiver = null
        }
        callIntent = Intent(this, ForegroundCallService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(callIntent)
        } else {
            startService(callIntent)
        }
    }

    fun stopBlocker() {
        if (callReceiver.isNotNull()) unregisterReceiver(callReceiver)
        if (callIntent.isNotNull()) stopService(callIntent)
    }

    fun isBlockerLaunched(): Boolean {
        return callReceiver.isNotNull() || callIntent.isNotNull()
    }

    private fun observeLiveData() {
        with(mainViewModel) {
            successAllDataLiveData.safeSingleObserve(this@MainActivity) {
                setMainProgressVisibility(false)
            }
            exceptionLiveData.safeSingleObserve(this@MainActivity) { errorMessage ->
                showMessage(errorMessage, true)
                setMainProgressVisibility(false)
            }
            progressStatusLiveData.safeSingleObserve(this@MainActivity) { mainProgress ->
                binding?.mainProgressBarAnimation?.mainProgress = mainProgress
            }
        }
    }

    fun showMessage(message: String, isError: Boolean) {
        binding?.hostMainFragment?.showMessage(message, isError)
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
            if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
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
            InterstitialAd.load(
                this,
                "ca-app-pub-3940256099942544/1033173712",
                it,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        interstitialAd = null
                        adIsLoading = false
                        val error =
                            "domain: ${adError.domain}, code: ${adError.code}, " + "message: ${adError.message}"
                        Toast.makeText(
                            this@MainActivity,
                            "onAdFailedToLoad() with error $error",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        adIsLoading = false
                    }
                }
            )
        }
    }

    override fun onBackPressed() {
        if (navController?.isBackPressedScreen().isTrue()) {
            navController?.navigate(MainNavigationDirections.startAppExitDialog())
        } else {
            navController?.popBackStack()
        }
    }
}