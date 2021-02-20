package com.animo.ru.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.App
import com.animo.ru.App.Companion.sendGetSpecialityAndRegions
import com.animo.ru.R
import com.animo.ru.models.Event
import com.animo.ru.models.answers.GetEventsAnswer
import com.animo.ru.ui.share.ShareBottomSheetDialog
import com.animo.ru.utilities.SpacesItemDecoration
import com.animo.ru.utilities.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventsFragment : Fragment(), EventsAdapter.OnEventsClickListener {
    private var recyclerView: RecyclerView? = null
    private var events: MutableMap<Int, Event>? = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.recyclerview_layout, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView!!.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.GreyBackground
            )
        )
        initRecyclerView()

        return view
    }

    override fun onStart() {
        super.onStart()
        getEvents()

        if (App.accessRegions == null || App.accessSpeciality == null) {
            sendGetSpecialityAndRegions()
        }
    }

    private fun initRecyclerView() {
        recyclerView!!.addItemDecoration(SpacesItemDecoration(10, 10))
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager =
            LinearLayoutManager(recyclerView!!.context, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.adapter = events?.let { EventsAdapter(it, this) }
    }

    override fun onEventClick(event: Event, id: Int) {
        val myBottomSheet: ShareBottomSheetDialog =
            ShareBottomSheetDialog.newInstance(
                id,
                event.share_title,
                event.text,
                "event"
            )

        myBottomSheet.show(childFragmentManager, myBottomSheet.tag)
    }

    private fun getEvents() {
        App.mService.getEvents(App.user.token!!, App.user.getRolesArrayName())
            .enqueue(
                object : Callback<GetEventsAnswer> {
                    override fun onFailure(call: Call<GetEventsAnswer>, t: Throwable) {
                        showToast(getString(R.string.error_server_lost))
                    }

                    override fun onResponse(
                        call: Call<GetEventsAnswer>,
                        response: Response<GetEventsAnswer>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            if (response.body()!!.status == 200.toShort()) {
                                events = response.body()!!.data
                                (recyclerView!!.adapter as EventsAdapter).update(events!!)

                            } else
                                response.body()!!.text?.let { showToast(it) }
                        }
                    }
                })
    }
}