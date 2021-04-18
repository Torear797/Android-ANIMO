package com.animo.ru.ui.preparations.infoPackage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.InfoPackage
import com.animo.ru.models.answers.GetInfoPackageAnswer
import com.animo.ru.ui.share.ShareBottomSheetDialog
import com.animo.ru.utilities.SpacesItemDecoration
import com.animo.ru.utilities.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "medicationId"
private const val ARG_PARAM2 = "infoPackageId"

class InfoPackageFragment : Fragment(), InfoPackageAdapter.OnInfoPackageClickListener {

    private var medicationId: Int? = null
    private var infoPackageId: Int? = null
    private var infoPackages: MutableMap<Int, InfoPackage>? = mutableMapOf()
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            medicationId = it.getInt(ARG_PARAM1)
            infoPackageId = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.recyclerview_layout, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        initRecyclerView()

        return view
    }

    override fun onStart() {
        super.onStart()
        getInfoPackages()
    }

    private fun initRecyclerView() {
        recyclerView!!.addItemDecoration(SpacesItemDecoration(10, 10))
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager =
            LinearLayoutManager(recyclerView!!.context, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.adapter = infoPackages?.let { InfoPackageAdapter(it, this) }
    }

    private fun getInfoPackages() {
        App.mService.getInfoPackages(medicationId!!, App.user.token!!)
            .enqueue(
                object : Callback<GetInfoPackageAnswer> {
                    override fun onFailure(call: Call<GetInfoPackageAnswer>, t: Throwable) {
                        showToast(getString(R.string.error_server_lost))
                    }

                    override fun onResponse(
                        call: Call<GetInfoPackageAnswer>,
                        response: Response<GetInfoPackageAnswer>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            if (response.body()!!.status == 200.toShort()) {
                                infoPackages = response.body()!!.packages
                                (recyclerView!!.adapter as InfoPackageAdapter).update(infoPackages!!)

                                if (infoPackageId != 0 && infoPackageId != null && recyclerView != null) {
                                    val id =
                                        (recyclerView!!.adapter as InfoPackageAdapter).getPositionForId(
                                            infoPackageId!!
                                        )
                                    (recyclerView!!.layoutManager as LinearLayoutManager).scrollToPosition(
                                        id
                                    )
                                }
                            } else
                                response.body()!!.text?.let { showToast(it) }
                        }
                    }
                })
    }

    override fun onItemClick(infoPackage: InfoPackage, id: Int) {
        val myBottomSheet: ShareBottomSheetDialog =
            ShareBottomSheetDialog.newInstance(
                id,
                infoPackage.share_title,
                infoPackage.share_description,
                "info_package"
            )

        myBottomSheet.show(childFragmentManager, myBottomSheet.tag)
    }
}