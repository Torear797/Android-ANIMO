package com.animo.ru.ui.preparations.infoPackage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.animo.ru.R

private const val ARG_PARAM1 = "medicationId"
private const val ARG_PARAM2 = "infoPackageId"

class InfoPackageFragment : Fragment() {

    private var medicationId: Int? = null
    private var infoPackageId: Int? = null

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
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_package, container, false)
    }

    companion object {

        fun newInstance(param1: Int, param2: Int) =
            InfoPackageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, param2)
                }
            }
    }
}