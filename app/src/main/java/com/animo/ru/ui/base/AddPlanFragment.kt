package com.animo.ru.ui.base

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.GuideData
import com.animo.ru.models.answers.BaseAnswer
import com.animo.ru.models.answers.GuideDataAnswer
import com.animo.ru.utilities.GuideSpinnerAdapter
import com.animo.ru.utilities.showToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private var selectedDoctors = ArrayList<Int>()
private var selectedPharmacy = ArrayList<Int>()

private var datePlanEditText: TextInputEditText? = null
private var noteEditText: TextInputEditText? = null
private var activitySpinner: Spinner? = null

private val activityList = HashMap<Int, GuideData>()

class AddPlanFragment : BottomSheetDialogFragment() {

    var cal = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_add_to_plan, container, false)

        val datePlanLayout: TextInputLayout = view.findViewById(R.id.textInputLayoutPlanDate)

        val sendBtn: MaterialButton = view.findViewById(R.id.send_btn)

        activitySpinner = view.findViewById(R.id.outvisit_activity)
        datePlanEditText = view.findViewById(R.id.plan_date)

        noteEditText = view.findViewById(R.id.plan_note)

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        datePlanLayout.setEndIconOnClickListener {
            this.context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }

        sendBtn.setOnClickListener {
            createNewPlan()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        if (activityList.isEmpty()) {
            this.context?.let { getOutvisitActivityData(it) }
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        datePlanEditText?.setText(sdf.format(cal.time))
    }

    companion object {
        fun newInstance(
            selectedDoctors: ArrayList<Int>,
            selectedPharmacy: ArrayList<Int>
        ) = AddPlanFragment().apply {
            arguments = Bundle(2).apply {
                putIntegerArrayList("selectedDoctors", selectedDoctors)
                putIntegerArrayList("selectedPharmacy", selectedPharmacy)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedDoctors = arguments?.getIntegerArrayList("selectedDoctors") as ArrayList<Int>
        selectedPharmacy = arguments?.getIntegerArrayList("selectedPharmacy") as ArrayList<Int>

    }

    private fun getOutvisitActivityData(context: Context) {
        App.user.token?.let { it ->
            App.mService.getActivityData(it).enqueue(
                object : Callback<GuideDataAnswer> {
                    override fun onFailure(call: Call<GuideDataAnswer>, t: Throwable) {
                        showToast(getString(R.string.error_server_lost))
                    }

                    override fun onResponse(
                        call: Call<GuideDataAnswer>,
                        response: Response<GuideDataAnswer>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            if (response.body()!!.status == 200.toShort()) {

                                val list = arrayListOf<GuideData>()
                                response.body()!!.data.forEach { (_, value) -> list.add(value) }

                                activitySpinner!!.adapter = GuideSpinnerAdapter(
                                    context,
                                    list
                                )
                            } else
                                response.body()!!.text?.let { showToast(it) }
                        }
                    }
                })
        }
    }

    private fun createNewPlan() {
        val selectedItem: GuideData = activitySpinner?.selectedItem as GuideData

        App.mService.createPlan(
            App.user.token!!,
            datePlanEditText?.text.toString(),
            "",
            "",
            selectedItem.id,
            noteEditText?.text.toString(),
            selectedDoctors,
            selectedPharmacy
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
}