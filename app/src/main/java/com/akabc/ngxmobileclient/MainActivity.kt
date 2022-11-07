package com.akabc.ngxmobileclient

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.akabc.ngxmobileclient.databinding.ActivityMainBinding
import com.akabc.ngxmobileclient.ui.login.LoginFragment
import com.akabc.ngxmobileclient.ui.login.data.model.Captcha
import com.akabc.ngxmobileclient.ui.login.data.model.LoggedInUser
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory((application as NewApplication).repository)
    }
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        //修改状态栏颜色
        WindowCompat.setDecorFitsSystemWindows(window, true)/*
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }*/
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return

        /**  获取本地存储的登录用户信息 **/
        getUser(sharedPref)

        binding.appBarMain.fab.setOnClickListener {
            val loginFragment = LoginFragment()
            loginFragment.show(supportFragmentManager, "login")
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val headerView =
            navView.inflateHeaderView(R.layout.nav_header_main)
        val navController =
            findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_dashboard,
            R.id.nav_history,
            R.id.nav_folder,
            R.id.nav_app), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val headIv: ImageView =
            headerView.findViewById(R.id.imageView_head)
        headIv.setOnClickListener {
            Log.d("test", "successful")
            val loginFragment = LoginFragment()
            loginFragment.show(supportFragmentManager, "login")
        }

        mainViewModel.loginResult.observe(this) { loginResult ->
            loginResult.success?.let {
                with(sharedPref.edit()) {
                    putString("userId", it.userId)
                    putString("displayName", it.displayName)
                    putString("token", it.token)
                    putInt("exceptionTime", it.exceptionTime)
                    putString("pwd", it.pwd)
                    putString("ctId", it.captcha.ctId)
                    putString("ctCode", it.captcha.ctCode)
                    apply()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getUser(sharedPref: SharedPreferences) {
        val keys = mutableListOf("userId",
            "displayName",
            "token",
            "exceptionTime",
            "pwd",
            "ctId",
            "ctCode",
            "ip","port")
        try {
            val user = LoggedInUser(
                sharedPref.getString(keys[0], "").toString(),
                sharedPref.getString(keys[1], "").toString(),
                sharedPref.getString(keys[2], "").toString(),
                sharedPref.getInt(keys[3], 0),
                sharedPref.getString(keys[4], "").toString(),
                Captcha(
                    sharedPref.getString(keys[5], "").toString(),
                    sharedPref.getString(keys[6], "").toString()),
                sharedPref.getString(keys[7],"").toString(),
                sharedPref.getString(keys[8],"").toString(),
            )
            mainViewModel.setLoginResultForRecord(this, user)
        } catch (e: Exception) {

        }
    }
}