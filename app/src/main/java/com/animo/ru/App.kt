package com.animo.ru

import android.app.Application
import com.animo.ru.models.User
import com.orhanobut.hawk.Hawk

class App : Application() {

    companion object {
        lateinit var user: User
    }

    override fun onCreate() {
        super.onCreate()
        Hawk.init(this).build();
    }

}