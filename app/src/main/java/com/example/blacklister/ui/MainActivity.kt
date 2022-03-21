package com.example.blacklister.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.blacklister.MainNavigationDirections
import com.example.blacklister.R
import com.example.blacklister.constants.Constants
import com.example.blacklister.local.SharedPreferencesUtil
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.utils.ForegroundCallService
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var callIntent: Intent? = null
    private var navController: NavController? = null
    var bottomNavigationView: BottomNavigationView? = null
    var toolbar: androidx.appcompat.widget.Toolbar? = null
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

            this.currentBackStackEntry?.savedStateHandle?.getLiveData<BlackNumber>(Constants.BLACK_NUMBER)
                ?.observe(
                    this@MainActivity
                ) { _ ->
                    finish()
                }
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

    override fun onBackPressed() {
        if (navController?.currentDestination?.id == R.id.onBoardingFragment || navController?.currentDestination?.id == R.id.loginFragment || navController?.currentDestination?.id == R.id.callLogListFragment) {
            navController?.navigate(
                MainNavigationDirections.startInfoDialog(
                    blackNumber = BlackNumber("sdfdsfsdf")
                )
            )
        } else {
            super.onBackPressed()
        }
    }
}