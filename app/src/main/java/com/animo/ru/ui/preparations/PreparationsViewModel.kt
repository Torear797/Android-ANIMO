package com.animo.ru.ui.preparations

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.animo.ru.models.LastInfoPackage
import com.animo.ru.models.Medication
import java.util.*

class PreparationsViewModel : ViewModel() {
    var preparations: MutableLiveData<TreeMap<Int, Medication>> = MutableLiveData()
    var lastInfoPackages: MutableLiveData<TreeMap<Int, LastInfoPackage>> = MutableLiveData()
}