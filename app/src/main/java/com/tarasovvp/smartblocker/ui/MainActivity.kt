package com.tarasovvp.smartblocker.ui

import android.animation.Animator
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.MainNavigationDirections
import com.tarasovvp.smartblocker.R
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

    var navigationScreens = arrayListOf(
        R.id.callListFragment,
        R.id.contactListFragment,
        R.id.blockerListFragment,
        R.id.permissionListFragment
    )
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, @JvmSuppressWildcards Boolean>? ->
            if (isGranted?.values?.contains(false).isTrue()) {
                Toast.makeText(this, getString(R.string.give_all_permissions), Toast.LENGTH_SHORT)
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
            Log.e("exceptionTAG", "MainActivity exceptionReceiver exception $exception")
            mainViewModel.exceptionLiveData.postValue(exception)
            setProgressVisibility(false)
        }
        registerReceiver(exceptionReceiver, IntentFilter(Constants.EXCEPTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(callHandleReceiver)
        unregisterReceiver(exceptionReceiver)
        callReceiver?.apply {
            unregisterReceiver(this)
        }
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
        Log.e("getAllDataTAG",
            "MainActivity isOnBoardingSeen ${SharedPreferencesUtil.isOnBoardingSeen} isLoggedInUser ${
                SmartBlockerApp.instance?.isLoggedInUser().isTrue()
            } savedInstanceState ${savedInstanceState.isNull()}")
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
                        R.id.blockerListFragment
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
                R.id.blockerListFragment -> navController?.navigate(R.id.blockerListFragment)
                R.id.permissionListFragment -> navController?.navigate(R.id.permissionListFragment)
                R.id.contactListFragment -> navController?.navigate(R.id.contactListFragment)
                R.id.callListFragment -> navController?.navigate(R.id.callListFragment)
            }
            return@setOnItemSelectedListener true
        }
    }

    private fun setOnDestinationChangedListener() {
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            Log.e("destinationTAG",
                "MainActivity addOnDestinationChangedListener isDialog $isDialog binding?.title ${binding?.toolbar?.title} title ${toolbar?.title} toolbar?.navigationIcon ${toolbar?.navigationIcon}")
            if (navigationScreens.contains(destination.id) || R.id.loginFragment == destination.id) {
                toolbar?.navigationIcon = null
            } else {
                if (destination.navigatorName != DIALOG && isDialog.not()) {
                    toolbar?.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
                }
            }
            if (destination.navigatorName == DIALOG || isDialog) {
                isDialog = isDialog.not()
                return@addOnDestinationChangedListener
            }
            Log.e("destinationTAG", "MainActivity return@addOnDestinationChangedListener after")
            toolbar?.menu?.clear()
            toolbar?.isVisible =
                destination.id != R.id.onBoardingFragment && destination.id != R.id.loginFragment
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
                Log.e("getAllDataTAG", "MainActivity observeLiveData successAllDataLiveData ")
                setMainProgressVisibility(false)
            }
            exceptionLiveData.safeSingleObserve(this@MainActivity) { errorMessage ->
                showMessage(errorMessage, true)
                Log.e("getAllDataTAG",
                    "MainActivity exceptionLiveData setProgressVisibility(false)")
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
        Log.e("getAllDataTAG", "MainActivity setMainProgressVisibility isVisible")
    }

    fun setProgressVisibility(isVisible: Boolean) {
        Log.e("getAllDataTAG", "MainActivity setProgressVisibility isVisible $isVisible")
        binding?.progressBar?.isVisible = isVisible
    }

    fun getAllData() {
        if (checkPermissions().isTrue()) {
            Log.e("getAllDataTAG",
                "MainActivity getAllData if(checkPermissions()) setProgressVisibility(true) isChangingConfigurations $isChangingConfigurations isFinishing $isFinishing")
            setMainProgressVisibility(true)
            if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()) {
                Log.e("getAllDataTAG",
                    "MainActivity getCurrentUser BlackListerApp.instance?.isLoggedInUser().isTrue() getCurrentUser()")
                mainViewModel.getCurrentUser()
            } else {
                Log.e("getAllDataTAG",
                    "MainActivity getCurrentUser isLoggedInUser().not() getAllData()")
                mainViewModel.getAllData()
            }
        } else {
            requestPermissionLauncher.launch(PermissionUtil.permissionsArray())
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