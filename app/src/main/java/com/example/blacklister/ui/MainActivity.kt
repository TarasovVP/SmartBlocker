package com.example.blacklister.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.blacklister.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = (supportFragmentManager.findFragmentById(
            R.id.host_main_fragment
        ) as NavHostFragment).navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigationView.isVisible =
                destination.id == R.id.callLogListFragment || destination.id == R.id.contactListFragment || destination.id == R.id.numberListFragment || destination.id == R.id.settingsFragment
        }
    }
}