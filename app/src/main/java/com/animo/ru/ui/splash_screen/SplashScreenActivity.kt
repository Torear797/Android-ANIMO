package com.animo.ru.ui.splash_screen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.animo.ru.App
import com.animo.ru.models.User
import com.animo.ru.ui.authorization.LoginActivity
import com.animo.ru.ui.menu.MenuActivity
import com.orhanobut.hawk.Hawk

class SplashScreenActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val saveUser = Hawk.get<User>("user", null)
        if(saveUser != null){
            App.user = saveUser
            startActivity(Intent(this, MenuActivity::class.java))
        } else
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}