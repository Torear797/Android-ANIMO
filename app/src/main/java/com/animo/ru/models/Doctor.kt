package com.animo.ru.models

import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.answers.BaseAnswer
import com.animo.ru.ui.base.DoctorsAdapter
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Doctor(
    @SerializedName("fio")
    val fio: String,
    @SerializedName("ov")
    val ov: String,
    @SerializedName("dv")
    val dv: String,
    @SerializedName("countVisits")
    val countVisits: Int,
    @SerializedName("specName")
    val specName: String,
    @SerializedName("organization")
    val organization: String,
    @SerializedName("regionName")
    val regionName: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("lpu")
    val lpu: String,
    @SerializedName("post")
    val post: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("city_phone")
    val city_phone: String,
    @SerializedName("ol")
    val ol: String,
    @SerializedName("IMS")
    val IMS: String,
    @SerializedName("isActive")
    val isActive: String,
    @SerializedName("birthday")
    val birthday: String,
    @SerializedName("note")
    val note: String,
    var isOpen: Boolean = false
) {
    fun attach(
        docId: Int, context: Context, recyclerView: RecyclerView,
        position: Int
    ) {
        App.mService.attachDoctor(App.user.token!!, docId)
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
                                (recyclerView.adapter as DoctorsAdapter).removeItem(docId, position)
                            } else
                                response.body()!!.text?.let {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                })
    }

}