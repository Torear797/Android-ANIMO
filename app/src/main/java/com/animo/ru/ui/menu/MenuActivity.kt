package com.animo.ru.ui.menu

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.animo.ru.App
import com.animo.ru.R
import com.google.android.material.navigation.NavigationView


class MenuActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.nav_host_fragment)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_search_doctors,
                R.id.nav_search_pharmacy,
                R.id.nav_cur_visits,
                R.id.nav_profile,
                R.id.nav_logout,
                R.id.nav_preparations
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_logout -> {
                    App.logout(applicationContext, this)
                }
            }
        }


        navView.setupWithNavController(navController)

        val header: View = navView.getHeaderView(0)
        val fio: TextView = header.findViewById(R.id.nav_header_fio)
        val role: TextView = header.findViewById(R.id.nav_header_role)
        val avatarText: TextView = header.findViewById(R.id.avatar_textView)
        fio.text = "Здравствуйте, ${App.user.first_name}"
        role.text = "Ваша Роль: ${App.user.getRoleString()}"
        avatarText.text = App.user.getInitials()


    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

}