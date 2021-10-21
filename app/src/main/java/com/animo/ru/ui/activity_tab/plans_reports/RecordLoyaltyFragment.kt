package com.animo.ru.ui.activity_tab.plans_reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.GuideData
import com.animo.ru.models.Plan
import com.animo.ru.models.answers.RecordLoyaltyFormDataAnswer
import com.animo.ru.ui.base.DoctorsAdapter
import com.animo.ru.utilities.GuideSpinnerAdapter
import com.animo.ru.utilities.SpacesItemDecoration
import com.animo.ru.utilities.showToast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecordLoyaltyFragment : Fragment(), RecordLoyaltyDataAdapter.OnItemClickListener {

    private val doctorsList: MutableList<GuideData> = ArrayList()
    private var specNameEditText: TextInputEditText? = null
    private var recyclerView: RecyclerView? = null
    private var planId: Int? = null
    private var plan: Plan? = null

    private val recordLoyaltyViewModel: RecordLoyaltyViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            doctorsList.addAll(it.getParcelableArrayList<GuideData>("doctors") as ArrayList<GuideData>)
            planId = it.getInt("planId")
            plan = it.getSerializable("plan") as Plan
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_record_loyalty, container, false)

        val sendToReportBtn: MaterialButton = view.findViewById(R.id.send_btn)

        sendToReportBtn.setOnClickListener {
            if (planId != null && plan != null) {
                context?.let { it1 -> plan!!.sendToReportInLoyaltyForm(planId!!, it1) }
            }
        }

        val doctorsSpinner: Spinner = view.findViewById(R.id.doctors)
        specNameEditText = view.findViewById(R.id.specName)

        doctorsSpinner.adapter = this.context?.let {
            GuideSpinnerAdapter(
                it,
                doctorsList as ArrayList<GuideData>
            )
        }

        doctorsSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {
                getLoyaltyForm(doctorsList[selectedItemPosition].id)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        initRecyclerView()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recordLoyaltyViewModel.loyaltyDataList.observe(viewLifecycleOwner, {
            recordLoyaltyViewModel.loyaltyDataList.value?.let { it1 ->
                (recyclerView?.adapter as RecordLoyaltyDataAdapter).update(it1)
            }
        })

    }

    private fun initRecyclerView() {
        recyclerView!!.addItemDecoration(SpacesItemDecoration(10, 10))
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager =
            LinearLayoutManager(recyclerView!!.context, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.adapter = RecordLoyaltyDataAdapter(mutableMapOf(), this)
    }

    private fun getLoyaltyForm(idDoctor: Int) {
        App.user.token?.let { it ->
            App.mService.getSegmentLoyaltyFormData(it, idDoctor).enqueue(
                object : Callback<RecordLoyaltyFormDataAnswer> {
                    override fun onFailure(call: Call<RecordLoyaltyFormDataAnswer>, t: Throwable) {
                        showToast(getString(R.string.error_server_lost))
                    }

                    override fun onResponse(
                        call: Call<RecordLoyaltyFormDataAnswer>,
                        response: Response<RecordLoyaltyFormDataAnswer>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            if (response.body()!!.status == 200.toShort()) {
                                specNameEditText?.setText(response.body()!!.specialtyName)
                                recordLoyaltyViewModel.loyaltyDataList.value?.clear()
                                recordLoyaltyViewModel.loyaltyDataList.postValue(response.body()!!.loyaltyData[0].toMutableMap())
                            } else
                                response.body()!!.text?.let { showToast(it) }
                        }
                    }
                })
        }
    }

    override fun onChangeQuestion() {
        TODO("Not yet implemented")
    }

}