package com.tarasovvp.blacklister.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.tarasovvp.blacklister.MainNavigationDirections
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.utils.BackPressedUtil.isBackPressedScreen
import com.tarasovvp.blacklister.utils.ForegroundCallService
import com.google.android.material.bottomnavigation.BottomNavigationView

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Blacklister)
        setContentView(R.layout.activity_main)
        startService()
        navController = (supportFragmentManager.findFragmentById(
            R.id.host_main_fragment
        ) as NavHostFragment).navController

        navController?.apply {
            val navGraph = this.navInflater.inflate(R.navigation.navigation)
            navGraph.setStartDestination(
                if (SharedPreferencesUtil.isOnBoardingSeen) {
                    R.id.loginFragment
                } else {
                    R.id.onBoardingFragment
                }
            )
            this.graph = navGraph

            bottomNavigationView = findViewById(R.id.bottom_nav)
            bottomNavigationView?.setupWithNavController(this)

            toolbar = findViewById(R.id.toolbar)
            toolbar?.setupWithNavController(this)
        }
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
        }
        mainViewModel.getAllData()
    }

    override fun onBackPressed() {
        if (navController?.isBackPressedScreen() == true) {
            navController?.navigate(MainNavigationDirections.startInfoDialog())
        } else {
            navController?.popBackStack()
        }
    }
}