package com.animo.ru.ui.activity_tab.plans_reports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.R
import com.animo.ru.models.LoyaltyForm
import com.animo.ru.utilities.SpacesItemDecoration
import com.animo.ru.utilities.deleteListener

class RecordLoyaltyDataAdapter(
    private val loyaltyDataList: MutableMap<String, LoyaltyForm>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RecordLoyaltyDataAdapter.RecordLoyaltyDataHolder>() {

    interface OnItemClickListener {
        fun onChangeQuestion()
    }

    inner class RecordLoyaltyDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LoyaltyQuestionsAdapter.OnItemClickListener  {
        private val medicationName: TextView = itemView.findViewById(R.id.medicationName)
        val questionsList: RecyclerView = itemView.findViewById(R.id.questionsList)

        val btnArrow: ImageView = itemView.findViewById(R.id.arrow)

        fun bind(data: LoyaltyForm) {
            medicationName.text = data.id

            questionsList.addItemDecoration(SpacesItemDecoration(10, 10))
            questionsList.itemAnimator = DefaultItemAnimator()
            questionsList.setHasFixedSize(true)
            questionsList.layoutManager =
                LinearLayoutManager(questionsList.context, LinearLayoutManager.VERTICAL, false)
            questionsList.adapter = LoyaltyQuestionsAdapter(data.questions.toMutableMap(), this)
        }

        override fun onChangeOption() {
            TODO("Not yet implemented")
        }
    }

    override fun onViewAttachedToWindow(holder: RecordLoyaltyDataHolder) {
        super.onViewAttachedToWindow(holder)

        holder.btnArrow.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                if (holder.questionsList.visibility == View.VISIBLE) {
                    holder.questionsList.visibility = View.GONE
                    holder.btnArrow.animate().rotation(0F)
                } else {
                    holder.questionsList.visibility = View.VISIBLE
                    holder.btnArrow.animate().rotation(90F)
                }
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: RecordLoyaltyDataHolder) {
        super.onViewDetachedFromWindow(holder)
        deleteListener(holder.btnArrow)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecordLoyaltyDataAdapter.RecordLoyaltyDataHolder {
        return RecordLoyaltyDataHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loyalty_data, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: RecordLoyaltyDataAdapter.RecordLoyaltyDataHolder,
        position: Int
    ) {
        val data = loyaltyDataList[getPositionKey(position)]
        data?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = loyaltyDataList.size

    private fun getPositionKey(position: Int): String {
        var curIndex = 0
        for ((key, _) in loyaltyDataList) {
            if (curIndex == position) return key
            curIndex++
        }

        return "0"
    }

    fun update(modelList: Map<String, LoyaltyForm>) {
        loyaltyDataList.clear()
        loyaltyDataList.putAll(modelList)
        notifyDataSetChanged()
    }
}