package com.tarasovvp.blacklister.ui

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.MainNavigationDirections
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.extensions.setAppLocale
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.utils.BackPressedUtil.isBackPressedScreen
import com.tarasovvp.blacklister.utils.ForegroundCallService
import com.tarasovvp.blacklister.utils.PermissionUtil
import com.tarasovvp.blacklister.utils.PermissionUtil.checkPermissions

class MainActivity : AppCompatActivity() {

    private var callIntent: Intent? = null
    private var navController: NavController? = null
    var bottomNavigationView: BottomNavigationView? = null
    var toolbar: androidx.appcompat.widget.Toolbar? = null
    val mainViewModel: MainViewModel by viewModels()
    var navigationScreens = arrayListOf(
        R.id.callLogListFragment,
        R.id.contactListFragment,
        R.id.blackNumberListFragment,
        R.id.settingsFragment
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Blacklister)
        setContentView(R.layout.activity_main)
        navController = (supportFragmentManager.findFragmentById(
            R.id.host_main_fragment
        ) as NavHostFragment).navController
        Log.e("localeTAG", "MainActivity onCreate")
        navController?.apply {
            val navGraph = this.navInflater.inflate(R.navigation.navigation)
            navGraph.setStartDestination(
                when {
                    !SharedPreferencesUtil.isOnBoardingSeen -> R.id.onBoardingFragment
                    BlackListerApp.instance?.auth?.currentUser.isNotNull() -> R.id.callLogListFragment
                    else -> R.id.loginFragment
                }
            )
            this.graph = navGraph

            bottomNavigationView = findViewById(R.id.bottom_nav)
            bottomNavigationView?.setupWithNavController(this)

            toolbar = findViewById(R.id.toolbar)
            toolbar?.setupWithNavController(this)
        }
        if (BlackListerApp.instance?.auth?.currentUser.isNotNull()) {
            getAllData()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        Log.e("localeTAG", "MainActivity attachBaseContext")
        super.attachBaseContext(ContextWrapper(newBase.setAppLocale(SharedPreferencesUtil.appLang)))
    }

    fun startService() {
        callIntent = Intent(this, ForegroundCallService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(callIntent)
        } else {
            startService(callIntent)
        }
    }

    fun stopService() {
        stopService(callIntent)
    }

    fun getAllData() {
        with(mainViewModel) {
            successLiveData.safeSingleObserve(this@MainActivity, {
                Log.e(
                    "mainViewModelTAG",
                    "MainActivity success $it"
                )
            })
            if (checkPermissions().isTrue()) {
                getAllData()
            } else {
                requestPermissionLauncher.launch(PermissionUtil.permissionsArray())
            }
        }
    }

    override fun onBackPressed() {
        if (navController?.isBackPressedScreen().isTrue()) {
            navController?.navigate(MainNavigationDirections.startInfoDialog())
        } else {
            navController?.popBackStack()
        }
    }
}