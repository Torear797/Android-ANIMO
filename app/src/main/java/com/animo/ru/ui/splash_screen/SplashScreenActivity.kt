package com.animo.ru.ui.splash_screen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.User
import com.animo.ru.models.answers.LoginAnswer
import com.animo.ru.retrofit.Common
import com.animo.ru.retrofit.RetrofitServices
import com.animo.ru.ui.authorization.LoginActivity
import com.animo.ru.ui.menu.MenuActivity
import com.animo.ru.utilities.showToast
import com.orhanobut.hawk.Hawk
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashScreenActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val saveUser = Hawk.get<User>("user", null)
        if(saveUser != null) {
            App.user = saveUser
            sendReLoginRequest()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun sendReLoginRequest() {
        App.user.token?.let {
            App.mService.reLogin(it, Build.MANUFACTURER + " " + Build.MODEL, "Android").enqueue(
                object : Callback<LoginAnswer> {
                    override fun onFailure(call: Call<LoginAnswer>, t: Throwable) {
                        showToast(getString(R.string.error_server_lost))
                    }

                    override fun onResponse(
                        call: Call<LoginAnswer>,
                        response: Response<LoginAnswer>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            if (response.body()!!.status == 200.toShort()) {
                                startActivity(Intent(applicationContext, MenuActivity::class.java))
                                finish()
                            }
                            else {
                                response.body()!!.text?.let { it -> showToast(it) }
                                startActivity(Intent(applicationContext, LoginActivity::class.java))
                                finish()
                            }
                        }
                    }
                })
        }
    }
}