package com.animo.ru.ui.activity_tab.plans_reports

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.Plan
import com.animo.ru.models.answers.GetPlansAnswer
import com.animo.ru.utilities.SpacesItemDecoration
import com.animo.ru.utilities.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PlansReportsFragment : Fragment(), PlansAdapter.OnPlansClickListener {
    private var recyclerView: RecyclerView? = null
    private var plans: MutableMap<Int, Plan>? = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.refresh_recyclerview_layout, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        recyclerView!!.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.GreyBackground
            )
        )
        initRecyclerView()


        swipeRefreshLayout.setOnRefreshListener {
            getData()
            swipeRefreshLayout.isRefreshing = false
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.only_search_menu, menu);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search) showToast("Поиск")
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        recyclerView!!.addItemDecoration(SpacesItemDecoration(10, 10))
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager =
            LinearLayoutManager(recyclerView!!.context, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.adapter = plans?.let { PlansAdapter(it, this) }
    }

    private fun getData() {
        App.mService.getPlans(App.user.token!!)
            .enqueue(
                object : Callback<GetPlansAnswer> {
                    override fun onFailure(call: Call<GetPlansAnswer>, t: Throwable) {
                        showToast(getString(R.string.error_server_lost))
                    }

                    override fun onResponse(
                        call: Call<GetPlansAnswer>,
                        response: Response<GetPlansAnswer>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            if (response.body()!!.status == 200.toShort()) {
                                plans = response.body()!!.arrPlans
                                (recyclerView!!.adapter as PlansAdapter).update(plans!!)

                            } else
                                response.body()!!.text?.let { showToast(it) }
                        }
                    }
                })
    }

    override fun onDeletePlan(plan: Plan, id: Int, position: Int) {
        this.context?.let { plan.deletePlan(id, it, recyclerView!!,position) }
    }

    override fun onSendPlan(plan: Plan, id: Int) {
        showToast("Отправить $id")
    }

}