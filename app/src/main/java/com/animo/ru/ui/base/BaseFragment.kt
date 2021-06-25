package com.animo.ru.ui.base

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.animo.ru.R
import com.animo.ru.models.Doctor
import com.animo.ru.models.Pharmacy
import com.animo.ru.utilities.SpacesItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*


class BaseFragment : Fragment() {
    private val mFragmentTitleList = listOf("Доктора", "Аптеки")

    private lateinit var viewpager: ViewPager2
    private lateinit var fab: FloatingActionButton

    private lateinit var countDoctors: TextView
    private lateinit var countPharmacy: TextView

    private val selectedDoctors = ArrayList<Int>()
    private val selectedPharmacy = ArrayList<Int>()

    private val baseViewModel: BaseViewModel by activityViewModels()

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_base, container, false)

        navController = findNavController()

        countDoctors = view.findViewById(R.id.countDoctorText)
        countPharmacy = view.findViewById(R.id.countPharmText)

        viewpager = view.findViewById(R.id.viewpager)
        viewpager.isUserInputEnabled = false

        fab = view.findViewById(R.id.floatingActionButton)

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)

        viewpager.adapter = ViewPagerAdapter()

        TabLayoutMediator(
            tabLayout, viewpager
        ) { tab, position ->
            tab.text = mFragmentTitleList[position]
        }.attach()


        updateCurrentPlanTable(isDoctorsChange = true, isPharmacyChange = true)

        fab.setOnClickListener {
            if (viewpager.currentItem == 0) {
                navController?.navigate(R.id.nav_edit_doctor, Bundle())
            } else {
                navController?.navigate(R.id.nav_edit_pharmacy, Bundle())
            }
        }

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
                    baseViewModel.searchPharmacy()
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

                    override fun onEditDoctor(doctorId: Int) {
                        val bundle = Bundle()
                        bundle.putInt("doctorId", doctorId)
                        navController?.navigate(R.id.nav_edit_doctor, bundle)
                    }
                }
            )

            recyclerView.addOnScrollListener(object : PaginationScrollListener(
                recyclerView.layoutManager as LinearLayoutManager
            ) {
                override fun isLastPage(): Boolean {
                    return baseViewModel.isLastDoctorPage
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
        } else {
            recyclerView.adapter = PharmacyAdapter(baseViewModel.pharmacyList,
                object : PharmacyAdapter.OnItemClickListener {
                    override fun onAttachPharmacy(pharmacy: Pharmacy, position: Int) {
                        pharmacy.attach(pharmacy.id, context!!, recyclerView, position)
                    }

                    override fun onAddToPlanPharmacy(pharmacyId: Int) {
                        if (!selectedPharmacy.contains(pharmacyId)) {
                            selectedPharmacy.add(pharmacyId)
                        } else {
                            selectedPharmacy.remove(pharmacyId)
                        }

                        updateCurrentPlanTable(isDoctorsChange = false, isPharmacyChange = true)
                    }

                    override fun onEditPharmacy(pharmacyId: Int) {
                        val bundle = Bundle()
                        bundle.putInt("pharmacyId", pharmacyId)
                        navController?.navigate(R.id.nav_edit_pharmacy, bundle)
                    }

                })

            recyclerView.addOnScrollListener(object : PaginationScrollListener(
                recyclerView.layoutManager as LinearLayoutManager
            ) {
                override fun isLastPage(): Boolean {
                    return baseViewModel.isLastPharmacyPage
                }

                override fun isLoading(): Boolean {
                    return baseViewModel.isLoadingPharmacy
                }

                override fun loadMoreItems() {
                    baseViewModel.searchPharmacy()
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

    private fun updateCurrentPlanTable(isDoctorsChange: Boolean, isPharmacyChange: Boolean) {
        if (isDoctorsChange) countDoctors.text = selectedDoctors.size.toString()
        if (isPharmacyChange) countPharmacy.text = selectedPharmacy.size.toString()
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
                baseViewModel.isLastPharmacyPage = true
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
                baseViewModel.isLastDoctorPage = true
            }
        }

        baseViewModel.searchDoctorOptions["start"] = (baseViewModel.doctors.size).toString()
    }
}