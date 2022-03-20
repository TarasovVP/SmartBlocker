package com.example.blacklister.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
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
    private var navigationScreens = arrayListOf(R.id.callLogListFragment, R.id.contactListFragment, R.id.numberListFragment, R.id.settingsFragment)

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

            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
            bottomNavigationView.setupWithNavController(this)

            val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            toolbar.setupWithNavController(this)

            this.addOnDestinationChangedListener { _, destination, _ ->
                if (destination.id == R.id.infoDialog) return@addOnDestinationChangedListener
                bottomNavigationView.isVisible = navigationScreens.contains(destination.id)
                toolbar.isVisible = navigationScreens.contains(destination.id)
                toolbar.navigationIcon = ContextCompat.getDrawable(this@MainActivity, if (destination.id == R.id.callLogListFragment) R.drawable.ic_arrow_transparent else R.drawable.ic_arrow_back)
                toolbar.setNavigationOnClickListener {
                    if (destination.id == R.id.callLogListFragment) return@setNavigationOnClickListener
                    this.popBackStack()
                }
            }

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