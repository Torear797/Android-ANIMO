package com.animo.ru

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import com.animo.ru.models.User
import com.animo.ru.retrofit.Common
import com.animo.ru.retrofit.RetrofitServices
import com.animo.ru.ui.authorization.LoginActivity
import com.orhanobut.hawk.Hawk

class App : Application() {

    companion object {
        lateinit var user: User
        lateinit var mService: RetrofitServices

        fun logout(context: Context, activity: FragmentActivity) {
            val login: String = Hawk.get("login")
            Hawk.deleteAll()
            Hawk.put("login", login)

            user = User()

            startActivity(context, Intent(context, LoginActivity::class.java), null)
            activity.finish()
        }
    }

    override fun onCreate() {
        super.onCreate()
        Hawk.init(this).build()
        mService = Common.retrofitService
    }

}