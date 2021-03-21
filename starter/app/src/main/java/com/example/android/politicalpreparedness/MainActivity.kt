package com.example.android.politicalpreparedness

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                navHost.navController.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
