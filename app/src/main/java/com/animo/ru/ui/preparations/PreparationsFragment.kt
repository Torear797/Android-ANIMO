package com.animo.ru.ui.preparations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
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
import com.animo.ru.room.AppDatabase
import com.animo.ru.utilities.SpacesItemDecoration
import com.animo.ru.utilities.isOnline
import com.animo.ru.utilities.showToast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PreparationsFragment : Fragment(), LastInfoPackageAdapter.OnItemClickListener,
    MedicationsAdapter.OnItemClickListener {

    private val mFragmentTitleList = listOf("Последние инфопакеты", "Препараты")

    private var currentPositionLastPackage = 0
    private var currentPositionPreparations = 0

    private lateinit var viewpager: ViewPager2
    private lateinit var curRecyclerViewLastPreparations: RecyclerView
    private lateinit var curRecyclerViewMedications: RecyclerView

    private var navController: NavController? = null

    private var db: AppDatabase? = null

    private val lastInfoPreparationsModel: PreparationsViewModel by activityViewModels()

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

        db = AppDatabase.getAppDataBase(context = this.requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lastInfoPreparationsModel.preparations.observe(viewLifecycleOwner, {
            viewpager.adapter!!.notifyDataSetChanged()
        })

        lastInfoPreparationsModel.lastInfoPackages.observe(viewLifecycleOwner, {
            viewpager.adapter!!.notifyDataSetChanged()
        })
    }

    override fun onStart() {
        super.onStart()

        if (isOnline(this.requireContext())) {
            if (accessRegions == null || accessSpeciality == null) {
                sendGetSpecialityAndRegions()
            }
            sendGetMedicationsDataRequest()
        } else {
            getOfflineData()
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
                if (!lastInfoPreparationsModel.lastInfoPackages.value.isNullOrEmpty() && !lastInfoPreparationsModel.preparations.value.isNullOrEmpty()) {
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
            recyclerView.adapter =
                lastInfoPreparationsModel.preparations.value?.let { MedicationsAdapter(it, this) }
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPosition(
                currentPositionPreparations
            )
            curRecyclerViewMedications = recyclerView

        } else {
            recyclerView.adapter = lastInfoPreparationsModel.lastInfoPackages.value?.let {
                LastInfoPackageAdapter(
                    it,
                    this
                )
            }
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPosition(
                currentPositionLastPackage
            )
            curRecyclerViewLastPreparations = recyclerView
        }
    }

    override fun onItemClick(medicationId: Int, jumpId: Int) {
        currentPositionLastPackage =
            (curRecyclerViewLastPreparations.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

        val bundle = Bundle()
        bundle.putInt("medicationId", medicationId)
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
                            lastInfoPreparationsModel.preparations.postValue(response.body()!!.arPreparations)
                            lastInfoPreparationsModel.lastInfoPackages.postValue(response.body()!!.arLastInfoPackage)

                            lastInfoPreparationsModel.preparations.value?.let {
                                lastInfoPreparationsModel.lastInfoPackages.value?.let { it1 ->
                                    insertOfflineData(
                                        it1, it
                                    )
                                }
                            }

                        } else
                            response.body()!!.text?.let { showToast(it) }
                    }
                }
            })
    }

    private fun getOfflineData() {
//        Observable.fromCallable {
//            db?.lastInfoPackageDao()?.getAll()
//        }.doOnNext { list ->
//            val offlineLastPreparations: TreeMap<Int, LastInfoPackage> = TreeMap()
//            list?.map {
//                offlineLastPreparations.put(it.id.toInt(), it)
//            }
//            lastInfoPreparationsModel.lastInfoPackages.postValue(offlineLastPreparations)
//
//        }.subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe()
//
//        Observable.fromCallable {
//            db?.medicationsDao()?.getAll()
//        }.doOnNext { list ->
//
//            val offlineMedications: TreeMap<Int, Medication> = TreeMap()
//            list?.map {
//                offlineMedications.put(it.id.toInt(), it)
//            }
//            lastInfoPreparationsModel.preparations.postValue(offlineMedications)
//
//        }.subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe()

        GlobalScope.launch(Dispatchers.IO) {
            val list = db?.lastInfoPackageDao()?.getAll()
            val offlineLastPreparations: TreeMap<Int, LastInfoPackage> = TreeMap()
            list?.map {
                offlineLastPreparations.put(it.id.toInt(), it)
            }
            lastInfoPreparationsModel.lastInfoPackages.postValue(offlineLastPreparations)

            val list2 = db?.medicationsDao()?.getAll()

            val offlineMedications: TreeMap<Int, Medication> = TreeMap()
            list2?.map {
                offlineMedications.put(it.id.toInt(), it)
            }
            lastInfoPreparationsModel.preparations.postValue(offlineMedications)
        }
    }

    private fun insertOfflineData(
        lastInfoPackages: MutableMap<Int, LastInfoPackage>,
        medications: MutableMap<Int, Medication>
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val list = mutableListOf<LastInfoPackage>()
            lastInfoPackages.forEach { (_, lastInfoPackage) ->
                list.add(lastInfoPackage)
            }
            db?.lastInfoPackageDao()?.insertAll(list)

            val list2 = mutableListOf<Medication>()
            medications.forEach { (_, item) ->
                list2.add(item)
            }
            db?.medicationsDao()?.insertAll(list2)
        }
    }
}