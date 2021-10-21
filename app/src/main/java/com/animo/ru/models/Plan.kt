package com.animo.ru.models

import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.answers.BaseAnswer
import com.animo.ru.ui.activity_tab.plans_reports.PlansAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class Plan (
    var user: String,
    var note: String,
    var dirVisit: String,
    var date: String,
    var dateWeek: String,
) : Serializable {
    fun deletePlan(
        planId: Int,
        context: Context,
        recyclerView: RecyclerView,
        position: Int
    ) {
        App.mService.deletePlan(App.user.token!!, planId)
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
                                (recyclerView.adapter as PlansAdapter).removeItem(planId, position)
                                Toast.makeText(context, response.body()!!.text, Toast.LENGTH_SHORT).show()
                            } else
                                response.body()!!.text?.let {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                })
    }

    fun sendToReportInList(
        planId: Int,
        context: Context,
        recyclerView: RecyclerView,
        position: Int
    ) {
        App.mService.sendPlanToReport(App.user.token!!, planId, 1)
            .enqueue(
                object : Callback<BaseAnswer> {
                    override fun onFailure(call: Call<BaseAnswer>, t: Throwable) {
                        Toast.makeText(context, R.string.error_server_lost, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<BaseAnswer>,
                        response: Response<BaseAnswer>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            if (response.body()!!.status == 200.toShort()) {
                                (recyclerView.adapter as PlansAdapter).removeItem(planId, position)
                                Toast.makeText(context, response.body()!!.text, Toast.LENGTH_SHORT).show()
                            } else
                                response.body()!!.text?.let {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                })
    }

    fun sendToReportInLoyaltyForm(
        planId: Int,
        context: Context
    ) {
        App.mService.sendPlanToReport(App.user.token!!, planId, 1)
            .enqueue(
                object : Callback<BaseAnswer> {
                    override fun onFailure(call: Call<BaseAnswer>, t: Throwable) {
                        Toast.makeText(context, R.string.error_server_lost, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<BaseAnswer>,
                        response: Response<BaseAnswer>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            if (response.body()!!.status == 200.toShort()) {
                                Toast.makeText(context, response.body()!!.text, Toast.LENGTH_SHORT).show()
                            } else
                                response.body()!!.text?.let {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                })
    }
}