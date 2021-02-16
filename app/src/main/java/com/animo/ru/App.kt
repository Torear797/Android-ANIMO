package com.animo.ru

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import com.animo.ru.models.DeviceInfo
import com.animo.ru.models.User
import com.animo.ru.models.answers.GetSpecAndRegAnswer
import com.animo.ru.retrofit.Common
import com.animo.ru.retrofit.RetrofitServices
import com.animo.ru.ui.authorization.LoginActivity
import com.animo.ru.utilities.showToast
import com.orhanobut.hawk.Hawk
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class App : Application() {

    companion object {
        lateinit var user: User
        lateinit var deviceInfo: DeviceInfo
        lateinit var mService: RetrofitServices
        var accessRegions: MutableMap<Int, String>? = null
        var accessSpeciality: MutableMap<Int, String>? = null

        fun logout(context: Context, activity: FragmentActivity) {
            val login: String = Hawk.get("login")
            Hawk.deleteAll()
            Hawk.put("login", login)

            user = User()

            startActivity(context, Intent(context, LoginActivity::class.java), null)
            activity.finish()
        }


         fun sendGetSpecialityAndRegions(){
            mService.getSpecAndReg(user.token!!).enqueue(
                object : Callback<GetSpecAndRegAnswer> {
                    override fun onFailure(call: Call<GetSpecAndRegAnswer>, t: Throwable) {}

                    override fun onResponse(
                        call: Call<GetSpecAndRegAnswer>,
                        response: Response<GetSpecAndRegAnswer>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            if (response.body()!!.status == 200.toShort()) {
                                accessRegions = response.body()!!.regions!!
                                accessSpeciality = response.body()!!.speciality!!
                            }
                        }
                    }
                })
        }
    }

    override fun onCreate() {
        super.onCreate()
        Hawk.init(this).build()
        mService = Common.retrofitService
    }

}