package com.animo.ru.ui.base

import android.os.Bundle
import android.view.*
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.PharmacyData
import com.animo.ru.models.answers.BaseAnswer
import com.animo.ru.models.answers.GetPharmacyDataAnswer
import com.animo.ru.utilities.showToast
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EditPharmacyFragment : Fragment() {
    private lateinit var name: TextInputEditText
    private lateinit var surname: TextInputEditText
    private lateinit var firstName: TextInputEditText
    private lateinit var patronymic: TextInputEditText
    private lateinit var region: Spinner
    private lateinit var city: TextInputEditText

    private var pharmacyId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            pharmacyId = it.getInt("pharmacyId")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.save_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                setPharmacyData()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_edit_pharmacy, container, false)

        name = view.findViewById(R.id.field_name)
        surname = view.findViewById(R.id.field_surname)
        firstName = view.findViewById(R.id.field_first_name)
        patronymic = view.findViewById(R.id.field_patronymic)
        region = view.findViewById(R.id.field_region)
        city = view.findViewById(R.id.field_city)

        return view
    }

    override fun onStart() {
        super.onStart()
        if (pharmacyId != 0) {
            getPharmacyData()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (pharmacyId == 0) {
            (activity as AppCompatActivity).supportActionBar?.title = "Добавление аптеки"
        }
    }

    private fun getPharmacyData() {
        App.mService.getPharmacyData(App.user.token!!, pharmacyId).enqueue(
            object : Callback<GetPharmacyDataAnswer> {
                override fun onFailure(call: Call<GetPharmacyDataAnswer>, t: Throwable) {
                    showToast(getString(R.string.error_server_lost))
                }

                override fun onResponse(
                    call: Call<GetPharmacyDataAnswer>,
                    response: Response<GetPharmacyDataAnswer>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.status == 200.toShort()) {
                            initFields(response.body()!!.pharmacyData)
                        } else
                            response.body()!!.text?.let { showToast(it) }
                    }
                }
            })
    }

    private fun setPharmacyData() {
        App.mService.savePharmacyData(
            App.user.token!!,
            pharmacyId,
            name.text.toString(),
            surname.text.toString(),
            firstName.text.toString(),
            patronymic.text.toString()
        ).enqueue(
            object : Callback<BaseAnswer> {
                override fun onFailure(call: Call<BaseAnswer>, t: Throwable) {
                    showToast(getString(R.string.error_server_lost))
                }

                override fun onResponse(
                    call: Call<BaseAnswer>,
                    response: Response<BaseAnswer>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        response.body()!!.text?.let { showToast(it) }
                    }
                }
            })
    }

    private fun initFields(pharmacy: PharmacyData) {
        name.setText(pharmacy.name)
        surname.setText(pharmacy.surname)
        firstName.setText(pharmacy.firstName)
        patronymic.setText(pharmacy.patronymic)

        /** HARDCODE - Скорее всего приложение никогда и никому непонадобиться. В противном случае - переработать*/
        if (pharmacy.region == "Москва") {
            region.setSelection(1)
        } else {
            region.setSelection(0)
        }

        city.setText(pharmacy.city)
    }
}