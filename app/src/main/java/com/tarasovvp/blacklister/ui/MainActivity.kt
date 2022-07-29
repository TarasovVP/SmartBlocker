package com.tarasovvp.blacklister.ui

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.MainNavigationDirections
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.utils.*
import com.tarasovvp.blacklister.utils.BackPressedUtil.isBackPressedScreen
import com.tarasovvp.blacklister.utils.PermissionUtil.checkPermissions
import java.util.*

class MainActivity : AppCompatActivity() {

    private var navController: NavController? = null
    var bottomNavigationView: BottomNavigationView? = null
    var toolbar: androidx.appcompat.widget.Toolbar? = null
    val mainViewModel: MainViewModel by viewModels()

    private var exceptionReceiver: ExceptionReceiver? = null
    private var callHandleReceiver: CallHandleReceiver? = null
    private var callIntent: Intent? = null
    private var callReceiver: CallReceiver? = null

    var navigationScreens = arrayListOf(
        R.id.callListFragment,
        R.id.contactListFragment,
        R.id.blackNumberListFragment,
        R.id.whiteNumberListFragment
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

    override fun onStart() {
        super.onStart()
        callHandleReceiver = CallHandleReceiver {
            mainViewModel.getAllData()
        }
        registerReceiver(callHandleReceiver, IntentFilter(Constants.CALL_RECEIVE))
        exceptionReceiver = ExceptionReceiver { exception ->
            mainViewModel.exceptionLiveData.postValue(exception)
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
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Blacklister)
        setContentView(R.layout.activity_main)
        navController = (supportFragmentManager.findFragmentById(
            R.id.host_main_fragment
        ) as NavHostFragment).navController
        navController?.apply {
            val navGraph = this.navInflater.inflate(R.navigation.navigation)
            navGraph.setStartDestination(
                when {
                    SharedPreferencesUtil.isOnBoardingSeen.not() -> R.id.onBoardingFragment
                    BlackListerApp.instance?.isLoggedInUser().isTrue() -> {
                        R.id.callListFragment
                    }
                    else -> R.id.loginFragment
                }
            )
            this.graph = navGraph

            bottomNavigationView = findViewById(R.id.bottom_nav)
            bottomNavigationView?.setupWithNavController(this)

            toolbar = findViewById(R.id.toolbar)
            toolbar?.setupWithNavController(this)
        }
        observeLiveData()
        if (SharedPreferencesUtil.isOnBoardingSeen) {
            getAllData()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper(newBase.setAppLocale(SharedPreferencesUtil.appLang ?: Locale.getDefault().language)))
    }

    fun startBlocker() {
        if (SharedPreferencesUtil.foreGround) {
            callIntent?.apply {
                stopService(this)
                callIntent = null
            }
            callReceiver = CallReceiver {
            }
            val filter = IntentFilter(Constants.PHONE_STATE)
            registerReceiver(callReceiver, filter)
        } else {
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
            }
            exceptionLiveData.safeSingleObserve(this@MainActivity) { errorMessage ->
                showMessage(errorMessage, true)
                isProgressProcess.postValue(false)
            }
            isProgressProcess.safeSingleObserve(this@MainActivity) { isVisible ->
                findViewById<ProgressBar>(R.id.progress_bar_container).isVisible = isVisible
            }
        }
    }

    fun showMessage(message: String, isError: Boolean) {
        findViewById<FrameLayout>(R.id.host_main_fragment).showMessage(message, isError)
    }

    fun getAllData() {
        if (checkPermissions().isTrue()) {
            if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
                mainViewModel.getCurrentUser()
            } else {
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