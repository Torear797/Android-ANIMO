package com.animo.ru.ui.splash_screen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.DeviceInfo
import com.animo.ru.models.User
import com.animo.ru.models.answers.LoginAnswer
import com.animo.ru.ui.authorization.LoginActivity
import com.animo.ru.ui.menu.MenuActivity
import com.animo.ru.utilities.isOnline
import com.animo.ru.utilities.showToast
import com.orhanobut.hawk.Hawk
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val saveUser = Hawk.get<User>("user", null)
        if (saveUser != null) {
            App.user = saveUser

            val saveDeviceInfo = Hawk.get<DeviceInfo>("deviceInfo", null)
            if (saveDeviceInfo != null) {
                App.deviceInfo = saveDeviceInfo
            }

            if (!isOnline(this)) {
                startActivity(Intent(this, MenuActivity::class.java))
                finish()
            } else {
                sendReLoginRequest()
            }

        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun sendReLoginRequest() {
        App.user.token?.let {
            App.user.refreshToken?.let { it1 ->
                App.mService.reLogin(it, it1, Build.MANUFACTURER + " " + Build.MODEL, "Android")
                    .enqueue(
                        object : Callback<LoginAnswer> {
                            override fun onFailure(call: Call<LoginAnswer>, t: Throwable) {
                                showToast(getString(R.string.error_server_lost))
                                App.logout(applicationContext, this@SplashScreenActivity)
                            }

                            override fun onResponse(
                                call: Call<LoginAnswer>,
                                response: Response<LoginAnswer>
                            ) {
                                if (response.isSuccessful && response.body() != null) {
                                    if (response.body()!!.status == 200.toShort()) {
                                        App.user.setNewTokens(
                                            response.body()!!.token,
                                            response.body()!!.exp,
                                            response.body()!!.refreshToken,
                                            response.body()!!.refreshExp
                                        )
                                        startActivity(
                                            Intent(
                                                applicationContext,
                                                MenuActivity::class.java
                                            )
                                        )
                                        finish()
                                    } else {
                                        response.body()!!.text?.let { it -> showToast(it) }
                                        App.logout(applicationContext, this@SplashScreenActivity)
//                                        startActivity(
//                                            Intent(
//                                                applicationContext,
//                                                LoginActivity::class.java
//                                            )
//                                        )
//                                        finish()
                                    }
                                }
                            }
                        })
            }
        }
    }
}