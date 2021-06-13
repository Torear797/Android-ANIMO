package com.animo.ru.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.activityViewModels
import com.animo.ru.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class SearchPharmacyFragment : BottomSheetDialogFragment() {

    private val baseViewModel: BaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_search_pharmacy, container, false)

        val searchAll: CheckBox = view.findViewById(R.id.searchAll)
        val searchBtn: MaterialButton = view.findViewById(R.id.searchBtn)

        searchAll.isChecked = baseViewModel.searchPharmacyOptions["searchAll"] != "true"

        searchAll.setOnCheckedChangeListener { _, isChecked ->
            baseViewModel.searchPharmacyOptions["searchAll"] = (!isChecked).toString()
        }

        searchBtn.setOnClickListener {
            baseViewModel.searchPharmacyOptions["start"] = "0"
            baseViewModel.pharmacyList.clear()
            baseViewModel.searchPharmacy()
        }

        return view
    }
}