package com.practise.furn_land.ui.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.practise.furn_land.R
import com.practise.furn_land.ui.fragments.CartFragmentDirections
import com.practise.furn_land.utils.ConnectionLiveData
import com.practise.furn_land.utils.safeNavigate
import com.practise.furn_land.view_models.OrderViewModel
import com.practise.furn_land.view_models.ProductListViewModel
import com.practise.furn_land.view_models.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomNavBar: BottomNavigationView
    private lateinit var productListViewModel: ProductListViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var navController: NavController
    private lateinit var snackBarLayout: CoordinatorLayout
    private lateinit var snackBar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        productListViewModel = ViewModelProvider(this)[ProductListViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        setUpUser()
        snackBarLayout = findViewById(R.id.snackBarLayout)
        setUpSnackBar()
        setUpConnectionMonitor()
        //Bottom Navigation View
        bottomNavBar = findViewById(R.id.bottomNav)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerHome) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener(getNavOnDestinationChangedListener())
        bottomNavBar.setupWithNavController(navController)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment,R.id.searchFragment,R.id.profileFragment))
        setupActionBarWithNavController(navController,appBarConfiguration)
    }

    private fun setUpUser() {
        val sharedPref = getSharedPreferences(getString(R.string.user_details), Context.MODE_PRIVATE)
        val userId = sharedPref?.getLong(getString(R.string.user_details),0)
        userId?.let {
            if (it != 0L) userViewModel.setLoggedInUser(userId)
        }
    }

    private fun getNavOnDestinationChangedListener() =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> showBottomNavBar()
                R.id.searchFragment -> showBottomNavBar()
                R.id.profileFragment -> showBottomNavBar()
                R.id.productListFragment -> hideBottomNavBar()
                R.id.newAddressDialogFragment-> hideBottomNavBar()
                else -> hideBottomNavBar()
            }
        }

    private fun showBottomNavBar() {
        bottomNavBar.visibility = View.VISIBLE
    }

    fun setBackButtonAs(@DrawableRes drawableIcon: Int){
        supportActionBar?.setHomeAsUpIndicator(drawableIcon)
    }

    private fun hideBottomNavBar() {
        bottomNavBar.visibility = View.GONE
    }

    fun setActionBarTitle(title: String){
        supportActionBar?.title = title
    }

    private fun setUpSnackBar() {
        snackBar = Snackbar.make(snackBarLayout,R.string.no_internet,Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction(R.string.okay){}
    }

    private fun setUpConnectionMonitor(){
        val connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this){ isAvailable ->
            if(isAvailable)
                snackBar.dismiss()
            else
                snackBar.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerHome) as NavHostFragment
        val navController = navHostFragment.navController
        return NavigationUI.navigateUp(navController,appBarConfiguration)
    }
}