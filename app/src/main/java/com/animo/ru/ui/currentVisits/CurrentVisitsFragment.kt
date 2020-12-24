package com.animo.ru.ui.currentVisits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.R
import com.animo.ru.models.Plan
import com.animo.ru.utilities.SpacesItemDecoration


class CurrentVisitsFragment : Fragment(), PlansAdapter.OnItemClickListener {

    private val visits: MutableList<Plan> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_current_visits, container, false)

        val visitsList = view.findViewById<RecyclerView>(R.id.visitsList)

        addTestData()
        initRecyclerView(visitsList)

        return view
    }

    private fun addTestData() {
        visits.add(Plan(1, "2020-10-30", "Бавыкина Варвара Александровна", "Пусто"))
        visits.add(Plan(2, "2020-10-29", "Бавыкина Варвара Александровна", "Пусто"))
        visits.add(Plan(3, "2020-10-28", "Бавыкина Варвара Александровна", "Пусто"))
        visits.add(Plan(4, "2020-10-27", "Бавыкина Варвара Александровна", "Пусто"))
    }

    private fun initRecyclerView(visitsList: RecyclerView) {
        visitsList.addItemDecoration(SpacesItemDecoration(10, 10))
        visitsList.setHasFixedSize(true)
        visitsList.itemAnimator = DefaultItemAnimator()
        visitsList.adapter = PlansAdapter(visits, this)
    }

    override fun onItemClick(clickPlan: Plan) {
        val myBottomSheet: CustomBottomSheetDialogFragment =
            CustomBottomSheetDialogFragment.newInstance(clickPlan)
        myBottomSheet.show(childFragmentManager, myBottomSheet.tag)
    }
}