package com.animo.ru.ui.base

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.Doctor
import com.animo.ru.models.Pharmacy
import com.animo.ru.models.answers.SearchPharmacyAnswer
import com.animo.ru.utilities.SpacesItemDecoration
import com.animo.ru.utilities.showToast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class BaseFragment : Fragment(), PharmacyAdapter.OnItemClickListener {
    private val mFragmentTitleList = listOf("Доктора", "Аптеки")

    private lateinit var viewpager: ViewPager2

    private var isLastDoctorPage: Boolean = false

    private var isLastPharmacyPage: Boolean = false
    private var isLoadingPharmacy: Boolean = false

    private lateinit var countDoctors: TextView
    private lateinit var countPharmacy: TextView

    private val selectedDoctors = ArrayList<Int>()
    private val selectedPharmacy = ArrayList<Int>()

    private val baseViewModel: BaseViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_base, container, false)

        countDoctors = view.findViewById(R.id.countDoctorText)
        countPharmacy = view.findViewById(R.id.countPharmText)

        viewpager = view.findViewById(R.id.viewpager)
        viewpager.isUserInputEnabled = false

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)

        viewpager.adapter = ViewPagerAdapter()

        TabLayoutMediator(
            tabLayout, viewpager
        ) { tab, position ->
            tab.text = mFragmentTitleList[position]
        }.attach()


        updateCurrentPlanTable(isDoctorsChange = true, isPharmacyChange = true)

        return view
    }

    internal inner class ViewPagerAdapter :
        RecyclerView.Adapter<ViewPagerAdapter.EventViewHolder>() {

        inner class EventViewHolder(val view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            EventViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_layout, parent, false)
            )

        override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
            (holder.view as? RecyclerView)?.also {
                initRecyclerView(it, position)

                if (position == 0) {
                    baseViewModel.searchDoctors()
                } else {
                    searchPharmacy()
                }
            }
        }

        override fun getItemCount(): Int {
            return mFragmentTitleList.size
        }
    }

    private fun initRecyclerView(recyclerView: RecyclerView, position: Int) {
        recyclerView.addItemDecoration(SpacesItemDecoration(10, 10))
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager =
            LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)

        if (position == 0) {
            recyclerView.adapter = DoctorsAdapter(baseViewModel.doctors,
                object : DoctorsAdapter.OnItemClickListener {
                    override fun onAttachDoctor(doctorId: Int, doctor: Doctor, position: Int) {
                        doctor.attach(doctorId, context!!, recyclerView, position)
                    }

                    override fun onAddToPlanDoctor(doctorId: Int) {
                        if (!selectedDoctors.contains(doctorId)) {
                            selectedDoctors.add(doctorId)
                        } else {
                            selectedDoctors.remove(doctorId)
                        }

                        updateCurrentPlanTable(isDoctorsChange = true, isPharmacyChange = false)
                    }

                }
            )

            recyclerView.addOnScrollListener(object : PaginationScrollListener(
                recyclerView.layoutManager as LinearLayoutManager
            ) {
                override fun isLastPage(): Boolean {
                    return isLastDoctorPage
                }

                override fun isLoading(): Boolean {
                    return baseViewModel.isLoadingDoctor
                }

                override fun loadMoreItems() {
                    baseViewModel.searchDoctors()
                }
            })

            baseViewModel.newDoctors.observe(viewLifecycleOwner, {
                insertNewDoctorsItems(recyclerView)
            })
        }
        else {
            recyclerView.adapter = PharmacyAdapter(baseViewModel.pharmacyList, this)

            recyclerView.addOnScrollListener(object : PaginationScrollListener(
                recyclerView.layoutManager as LinearLayoutManager
            ) {
                override fun isLastPage(): Boolean {
                    return isLastPharmacyPage
                }

                override fun isLoading(): Boolean {
                    return isLoadingPharmacy
                }

                override fun loadMoreItems() {
                    searchPharmacy()
                }
            })

            baseViewModel.newPharmacyList.observe(viewLifecycleOwner, {
                insertNewPharmacyItems(recyclerView)
            })
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.base_menu_toolbar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                if (viewpager.currentItem == 0) {
                    val myBottomSheet = SearchDoctorFragment()
                    myBottomSheet.show(childFragmentManager, myBottomSheet.tag)
                } else {
                    val myBottomSheet = SearchPharmacyFragment()
                    myBottomSheet.show(childFragmentManager, myBottomSheet.tag)
                }
            }
            R.id.createPlan -> {
                val myBottomSheet: AddPlanFragment =
                    AddPlanFragment.newInstance(selectedDoctors, selectedPharmacy)
                myBottomSheet.show(childFragmentManager, myBottomSheet.tag)
            }
        }

        return super.onOptionsItemSelected(item)
    }

//    override fun onAttachDoctor(doctorId: Int, doctor: Doctor) {
//        this.context?.let { doctor.attach(doctorId, it) }
//    }

//    override fun onAddToPlanDoctor(doctorId: Int) {
//        if (!selectedDoctors.contains(doctorId)) {
//            selectedDoctors.add(doctorId)
//        } else {
//            selectedDoctors.remove(doctorId)
//        }
//
//        updateCurrentPlanTable(isDoctorsChange = true, isPharmacyChange = false)
//    }

    private fun searchPharmacy() {
        isLoadingPharmacy = true
        App.mService.searchPharmacy(App.user.token!!, baseViewModel.searchPharmacyOptions).enqueue(
            object : Callback<SearchPharmacyAnswer> {
                override fun onFailure(call: Call<SearchPharmacyAnswer>, t: Throwable) {
                    showToast(getString(R.string.error_server_lost))
                }

                override fun onResponse(
                    call: Call<SearchPharmacyAnswer>,
                    response: Response<SearchPharmacyAnswer>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.status == 200.toShort()) {
                            baseViewModel.newPharmacyList.postValue(response.body()!!.pharmacy)
                        } else
                            response.body()!!.text?.let { showToast(it) }
                    }

                    isLoadingPharmacy = false
                }
            })
    }

    private fun updateCurrentPlanTable(isDoctorsChange: Boolean, isPharmacyChange: Boolean) {
        if (isDoctorsChange) countDoctors.text = selectedDoctors.size.toString()
        if (isPharmacyChange) countPharmacy.text = selectedPharmacy.size.toString()
    }

    override fun onAttachPharmacy(pharmacy: Pharmacy) {
        this.context?.let { pharmacy.attach(pharmacy.id, it) }
    }

    override fun onAddToPlanPharmacy(pharmacyId: Int) {
        if (!selectedPharmacy.contains(pharmacyId)) {
            selectedPharmacy.add(pharmacyId)
        } else {
            selectedPharmacy.remove(pharmacyId)
        }

        updateCurrentPlanTable(isDoctorsChange = false, isPharmacyChange = true)
    }

    private fun insertNewPharmacyItems(recyclerView: RecyclerView) {
        if (baseViewModel.searchPharmacyOptions["start"] == "0") {
            (recyclerView.adapter as PharmacyAdapter).update(baseViewModel.newPharmacyList.value!!)
        } else {
            if (baseViewModel.newPharmacyList.value!!.size > 0) {
                (recyclerView.adapter as PharmacyAdapter).insertItems(
                    baseViewModel.pharmacyList.size,
                    baseViewModel.newPharmacyList.value!!
                )
            }

            if (baseViewModel.newPharmacyList.value!!.size == 0 || baseViewModel.newPharmacyList.value!!.size < baseViewModel.searchPharmacyOptions["countOnPage"]!!.toInt()) {
                isLastPharmacyPage = true
            }
        }

        baseViewModel.searchPharmacyOptions["start"] = (baseViewModel.pharmacyList.size).toString()
    }

    private fun insertNewDoctorsItems(recyclerView: RecyclerView) {
        if (baseViewModel.searchDoctorOptions["start"] == "0") {
            (recyclerView.adapter as DoctorsAdapter).update(baseViewModel.newDoctors.value!!)
        } else {
            if (baseViewModel.newDoctors.value!!.size > 0) {
                (recyclerView.adapter as DoctorsAdapter).insertItems(
                    baseViewModel.doctors.size,
                    baseViewModel.newDoctors.value!!
                )
            }

            if (baseViewModel.newDoctors.value!!.size == 0 || baseViewModel.newDoctors.value!!.size < baseViewModel.searchDoctorOptions["countOnPage"]!!.toInt()) {
                isLastDoctorPage = true
            }
        }

        baseViewModel.searchDoctorOptions["start"] = (baseViewModel.doctors.size).toString()
    }
}