package com.animo.ru.ui.preparations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.animo.ru.App
import com.animo.ru.App.Companion.accessRegions
import com.animo.ru.App.Companion.accessSpeciality
import com.animo.ru.App.Companion.sendGetSpecialityAndRegions
import com.animo.ru.R
import com.animo.ru.models.LastInfoPackage
import com.animo.ru.models.Medication
import com.animo.ru.models.answers.MedicationDataAnswer
import com.animo.ru.utilities.SpacesItemDecoration
import com.animo.ru.utilities.showToast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PreparationsFragment : Fragment(), LastInfoPackageAdapter.OnItemClickListener,
    MedicationsAdapter.OnItemClickListener {
    private var lastInfoPackages: MutableMap<Int, LastInfoPackage>? = null
    private var preparations: MutableMap<Int, Medication>? = null
    private val mFragmentTitleList = listOf("Последние инфопакеты", "Препараты")

    private var currentPositionLastPackage = 0
    private var currentPositionPreparations = 0

    private lateinit var viewpager: ViewPager2
    private lateinit var curRecyclerViewLastPreparations: RecyclerView
    private lateinit var curRecyclerViewMedications: RecyclerView

    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_preparations, container, false)

        navController = findNavController()

        viewpager = view.findViewById(R.id.viewpager)
        val tabLayout: TabLayout = view.findViewById(R.id.prep_tab_layout)

        viewpager.adapter = ViewPagerAdapter()

        TabLayoutMediator(
            tabLayout, viewpager
        ) { tab, position ->
            tab.text = mFragmentTitleList[position]
        }.attach()

        return view
    }

    override fun onStart() {
        super.onStart()
        if (preparations == null && lastInfoPackages == null) {
            sendGetMedicationsDataRequest()
        }

        if (accessRegions == null || accessSpeciality == null) {
            sendGetSpecialityAndRegions()
        }
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
                if (!lastInfoPackages.isNullOrEmpty() && !preparations.isNullOrEmpty()) {
                    initRecyclerView(it, position)
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

        if (position == 1) {
            recyclerView.adapter = preparations?.let { MedicationsAdapter(it, this) }
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPosition(
                currentPositionPreparations
            )
            curRecyclerViewMedications = recyclerView

        } else {
            recyclerView.adapter = lastInfoPackages?.let { LastInfoPackageAdapter(it, this) }
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPosition(
                currentPositionLastPackage
            )
            curRecyclerViewLastPreparations = recyclerView
        }
    }


    override fun onItemClick(preparatId: Int, jumpId: Int) {
        currentPositionLastPackage =
            (curRecyclerViewLastPreparations.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

        val bundle = Bundle()
        bundle.putInt("medicationId", preparatId)
        bundle.putInt("infoPackageId", jumpId)
        navController?.navigate(R.id.nav_info_package, bundle)
    }

    override fun onItemClick(medicationId: Int) {
        currentPositionPreparations =
            (curRecyclerViewMedications.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

        val bundle = Bundle()
        bundle.putInt("medicationId", medicationId)
        bundle.putInt("infoPackageId", 0)
        navController?.navigate(R.id.nav_info_package, bundle)
    }

    private fun sendGetMedicationsDataRequest() {
        App.mService.getMedicationsData(App.user.token!!).enqueue(
            object : Callback<MedicationDataAnswer> {
                override fun onFailure(call: Call<MedicationDataAnswer>, t: Throwable) {
                    showToast(getString(R.string.error_server_lost))
                }

                override fun onResponse(
                    call: Call<MedicationDataAnswer>,
                    response: Response<MedicationDataAnswer>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.status == 200.toShort()) {
                            preparations = response.body()!!.arPreparations
                            lastInfoPackages = response.body()!!.arLastInfoPackage
                            viewpager.adapter!!.notifyDataSetChanged()
                        } else
                            response.body()!!.text?.let { showToast(it) }
                    }
                }
            })
    }
}