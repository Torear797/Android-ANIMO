package com.animo.ru.ui.base

import android.os.Bundle
import android.view.*
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.DoctorData
import com.animo.ru.models.answers.BaseAnswer
import com.animo.ru.models.answers.GetDoctorDataAnswer
import com.animo.ru.utilities.showToast
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditDoctorFragment : Fragment() {

    private lateinit var surname: TextInputEditText
    private lateinit var name: TextInputEditText
    private lateinit var patronymic: TextInputEditText
    private lateinit var region: Spinner
    private lateinit var city: TextInputEditText
    private lateinit var specialization: Spinner

    private var doctorId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            doctorId = it.getInt("doctorId")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.save_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                setDoctorData()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_edit_doctor, container, false)

        surname = view.findViewById(R.id.field_surname)
        name = view.findViewById(R.id.field_name)
        patronymic = view.findViewById(R.id.field_patronymic)
        region = view.findViewById(R.id.field_region)
        city = view.findViewById(R.id.field_city)
        specialization = view.findViewById(R.id.field_specialization)

        return view
    }

    override fun onStart() {
        super.onStart()
        if (doctorId != 0) {
            getDoctorData()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (doctorId == 0) {
            (activity as AppCompatActivity).supportActionBar?.title = "Добавление доктора"
        }
    }

    private fun getDoctorData() {
        App.mService.getDoctorData(App.user.token!!, doctorId).enqueue(
            object : Callback<GetDoctorDataAnswer> {
                override fun onFailure(call: Call<GetDoctorDataAnswer>, t: Throwable) {
                    showToast(getString(R.string.error_server_lost))
                }

                override fun onResponse(
                    call: Call<GetDoctorDataAnswer>,
                    response: Response<GetDoctorDataAnswer>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.status == 200.toShort()) {
                            initFields(response.body()!!.doctorData)
                        } else
                            response.body()!!.text?.let { showToast(it) }
                    }
                }
            })
    }

    private fun setDoctorData() {

        App.mService.saveDoctorData(
            App.user.token!!,
            doctorId,
            surname.text.toString(),
            name.text.toString(),
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

    private fun initFields(doctor: DoctorData) {
        surname.setText(doctor.surname)
        name.setText(doctor.name)
        patronymic.setText(doctor.patronymic)
//        region.setSelection(0)
        city.setText(doctor.city)
//        specialization.setSelection(0)
    }
}