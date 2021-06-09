package com.animo.ru.ui.base

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.Doctor
import com.animo.ru.models.Pharmacy
import com.animo.ru.models.answers.SearchDoctorsAnswer
import com.animo.ru.models.answers.SearchPharmacyAnswer
import com.animo.ru.ui.currentVisits.CustomBottomSheetDialogFragment
import com.animo.ru.utilities.SpacesItemDecoration
import com.animo.ru.utilities.showToast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

class BaseFragment : Fragment(), DoctorsAdapter.OnItemClickListener,
    PharmacyAdapter.OnItemClickListener {
    private val mFragmentTitleList = listOf("Доктора", "Аптеки")

    private lateinit var viewpager: ViewPager2

    private val doctors = TreeMap<Int, Doctor>()
    private val newDoctors = TreeMap<Int, Doctor>()
    private val searchDoctorOptions = HashMap<String, String>()

    private val pharmacyList = TreeMap<Int, Pharmacy>()
    private val newPharmacyList = TreeMap<Int, Pharmacy>()
    private val searchPharmacyOptions = HashMap<String, String>()

    private var isLastDoctorPage: Boolean = false
    private var isLoadingDoctor: Boolean = false

    private var isLastPharmacyPage: Boolean = false
    private var isLoadingPharmacy: Boolean = false

    private lateinit var countDoctors: TextView
    private lateinit var countPharmacy: TextView

    private val selectedDoctors = ArrayList<Int>()
    private val selectedPharmacy = ArrayList<Int>()

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
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)

        viewpager.adapter = ViewPagerAdapter()

        TabLayoutMediator(
            tabLayout, viewpager
        ) { tab, position ->
            tab.text = mFragmentTitleList[position]
        }.attach()

        searchDoctorOptions["start"] = 0.toString()
        searchDoctorOptions["countOnPage"] = 3.toString()

        searchPharmacyOptions["start"] = 0.toString()
        searchPharmacyOptions["countOnPage"] = 3.toString()
        searchPharmacyOptions["searchAll"] = "true"
        searchPharmacyOptions["active"] = "1"
        searchPharmacyOptions["region"] = App.user.getFirstRegionId().toString()
        searchPharmacyOptions["med_representative"] = App.user.id.toString()

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
                if (position == 0) {
                    searchDoctors(it)
                } else {
                    searchPharmacy(it)
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
            recyclerView.adapter = DoctorsAdapter(doctors, this)

            recyclerView.addOnScrollListener(object : PaginationScrollListener(
                recyclerView.layoutManager as LinearLayoutManager
            ) {
                override fun isLastPage(): Boolean {
                    return isLastDoctorPage
                }

                override fun isLoading(): Boolean {
                    return isLoadingDoctor
                }

                override fun loadMoreItems() {
                    searchDoctors(recyclerView)
                }
            })
        } else {
            recyclerView.adapter = PharmacyAdapter(pharmacyList, this)

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
                    searchPharmacy(recyclerView)
                }
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
                showToast("Поиск")
            }
            R.id.createPlan -> {
                val myBottomSheet: AddPlanFragment = AddPlanFragment.newInstance(selectedDoctors, selectedPharmacy)
                myBottomSheet.show(childFragmentManager, myBottomSheet.tag)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onAttachDoctor(doctorId: Int, doctor: Doctor) {
        this.context?.let { doctor.attach(doctorId, it) }
    }

    override fun onAddToPlanDoctor(doctorId: Int) {
        if (!selectedDoctors.contains(doctorId)) {
            selectedDoctors.add(doctorId)
        } else {
            selectedDoctors.remove(doctorId)
        }

        updateCurrentPlanTable(isDoctorsChange = true, isPharmacyChange = false)
    }

    private fun searchDoctors(recyclerView: RecyclerView) {
        isLoadingDoctor = true
        App.mService.searchDoctors(App.user.token!!, searchDoctorOptions).enqueue(
            object : Callback<SearchDoctorsAnswer> {
                override fun onFailure(call: Call<SearchDoctorsAnswer>, t: Throwable) {
                    showToast(getString(R.string.error_server_lost))
                }

                override fun onResponse(
                    call: Call<SearchDoctorsAnswer>,
                    response: Response<SearchDoctorsAnswer>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.status == 200.toShort()) {
                            newDoctors.clear()
                            newDoctors.putAll(response.body()!!.doctors)
                            insertNewDoctorsItems(recyclerView)
                        } else
                            response.body()!!.text?.let { showToast(it) }
                    }

                    isLoadingDoctor = false
                }
            })
    }

    private fun searchPharmacy(recyclerView: RecyclerView) {
        isLoadingPharmacy = true
        App.mService.searchPharmacy(App.user.token!!, searchPharmacyOptions).enqueue(
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
                            newPharmacyList.clear()
                            newPharmacyList.putAll(response.body()!!.pharmacy)
//                            (viewpager.adapter as ViewPagerAdapter).updatePharmacy()
                            insertNewPharmacyItems(recyclerView)
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
        if (pharmacyList.isEmpty() || recyclerView.adapter == null) {
            pharmacyList.putAll(newPharmacyList)
            initRecyclerView(recyclerView, 1)
        } else {
            if (searchPharmacyOptions["start"] == "0") {
                (recyclerView.adapter as PharmacyAdapter).update(newPharmacyList)
            } else {
                if (newPharmacyList.size > 0) {
                    (recyclerView.adapter as PharmacyAdapter).insertItems(
                        pharmacyList.size,
                        newPharmacyList
                    )
                }

                if (newPharmacyList.size == 0 || newPharmacyList.size < searchPharmacyOptions["countOnPage"]!!.toInt()) {
                    isLastPharmacyPage = true
                }
            }
        }

        searchPharmacyOptions["start"] = (pharmacyList.size).toString()
        newPharmacyList.clear()
    }

    private fun insertNewDoctorsItems(recyclerView: RecyclerView) {
        if (doctors.isEmpty() || recyclerView.adapter == null) {
            doctors.putAll(newDoctors)
            initRecyclerView(recyclerView, 0)
        } else {
            if (searchDoctorOptions["start"] == "0") {
                (recyclerView.adapter as DoctorsAdapter).update(newDoctors)
            } else {
                if (newDoctors.size > 0) {
                    (recyclerView.adapter as DoctorsAdapter).insertItems(
                        doctors.size,
                        newDoctors
                    )
                }

                if (newDoctors.size == 0 || newDoctors.size < searchDoctorOptions["countOnPage"]!!.toInt()) {
                    isLastDoctorPage = true
                }
            }
        }

        searchDoctorOptions["start"] = (doctors.size).toString()
        newDoctors.clear()
    }
}