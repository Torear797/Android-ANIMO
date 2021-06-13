package com.animo.ru.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.Doctor
import com.animo.ru.models.Pharmacy
import com.animo.ru.models.answers.SearchDoctorsAnswer
import com.animo.ru.models.answers.SearchPharmacyAnswer
import com.animo.ru.utilities.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

class BaseViewModel : ViewModel() {
    val searchDoctorOptions = HashMap<String, String>()
    val searchPharmacyOptions = HashMap<String, String>()

    var doctors = TreeMap<Int, Doctor>()
    var newDoctors: MutableLiveData<TreeMap<Int, Doctor>> = MutableLiveData()

    val pharmacyList = TreeMap<Int, Pharmacy>()
    var newPharmacyList: MutableLiveData<TreeMap<Int, Pharmacy>> = MutableLiveData()

    var isLoadingDoctor: Boolean = false
    var isLoadingPharmacy: Boolean = false

    var isLastDoctorPage: Boolean = false
    var isLastPharmacyPage: Boolean = false

    init {
        newDoctors.value = TreeMap<Int, Doctor>()
        newPharmacyList.value = TreeMap<Int, Pharmacy>()

        searchDoctorOptions["start"] = 0.toString()
        searchDoctorOptions["countOnPage"] = 3.toString()
        searchDoctorOptions["searchAll"] = "true"
        searchDoctorOptions["active"] = "1"

        searchPharmacyOptions["start"] = 0.toString()
        searchPharmacyOptions["countOnPage"] = 3.toString()
        searchPharmacyOptions["searchAll"] = "true"
        searchPharmacyOptions["active"] = "1"
        searchPharmacyOptions["region"] = App.user.getFirstRegionId().toString()
        searchPharmacyOptions["med_representative"] = App.user.id.toString()
    }

    fun searchDoctors() {
        isLoadingDoctor = true
        App.mService.searchDoctors(App.user.token!!, searchDoctorOptions).enqueue(
            object : Callback<SearchDoctorsAnswer> {
                override fun onFailure(call: Call<SearchDoctorsAnswer>, t: Throwable) {}

                override fun onResponse(
                    call: Call<SearchDoctorsAnswer>,
                    response: Response<SearchDoctorsAnswer>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.status == 200.toShort()) {
                            newDoctors.postValue(response.body()!!.doctors)
                        }
                    }

                    isLoadingDoctor = false
                }
            })
    }

    fun searchPharmacy() {
        isLoadingPharmacy = true
        App.mService.searchPharmacy(App.user.token!!, searchPharmacyOptions).enqueue(
            object : Callback<SearchPharmacyAnswer> {
                override fun onFailure(call: Call<SearchPharmacyAnswer>, t: Throwable) {}

                override fun onResponse(
                    call: Call<SearchPharmacyAnswer>,
                    response: Response<SearchPharmacyAnswer>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.status == 200.toShort()) {
                            newPharmacyList.postValue(response.body()!!.pharmacy)
                        }
                    }

                    isLoadingPharmacy = false
                }
            })
    }
}