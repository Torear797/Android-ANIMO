package com.animo.ru.ui.search.doctors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.animo.ru.R
import com.animo.test.ui.home.HomeViewModel

class SearchDoctorsFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_search_doctors, container, false)
        homeViewModel.text.observe(viewLifecycleOwner, {
//            text_home.text = it
        })
        return root
    }
}