package com.animo.ru.ui.activity_tab.plans_reports

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.animo.ru.models.LoyaltyForm
import java.util.*

class RecordLoyaltyViewModel: ViewModel() {
    var loyaltyDataList: MutableLiveData<MutableMap<String, LoyaltyForm>> = MutableLiveData()
}