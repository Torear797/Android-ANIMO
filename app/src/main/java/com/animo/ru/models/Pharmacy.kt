package com.animo.ru.models

import android.content.Context
import android.widget.Toast
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.answers.BaseAnswer
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Pharmacy (
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("fio")
    val fio: String,
    @SerializedName("post")
    val post: String,
    @SerializedName("lastVisit")
    val lastVisit: String,
    @SerializedName("countVisits")
    val countVisits: Int,
    @SerializedName("isNetworking")
    val isNetworking: String,
    @SerializedName("contract")
    val contract: String,
    @SerializedName("regionName")
    val regionName: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("isActive")
    val isActive: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("note")
    val note: String,
    var isOpen: Boolean = false
) {
    fun attach(pharmacyId: Int, context: Context) {
        App.mService.attachPharmacy(App.user.token!!, pharmacyId)
            .enqueue(
                object : Callback<BaseAnswer> {
                    override fun onFailure(call: Call<BaseAnswer>, t: Throwable) {
                        Toast.makeText(context, R.string.error_server_lost, Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onResponse(
                        call: Call<BaseAnswer>,
                        response: Response<BaseAnswer>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            if (response.body()!!.status == 200.toShort()) {
//                                Toast.makeText(context, response.body()!!.text, Toast.LENGTH_SHORT).show()
                            } else
                                response.body()!!.text?.let {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                })
    }
}