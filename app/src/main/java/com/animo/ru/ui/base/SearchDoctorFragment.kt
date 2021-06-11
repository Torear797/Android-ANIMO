package com.animo.ru.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.animo.ru.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchDoctorFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_search_doctors, container, false)


        return view
    }
}